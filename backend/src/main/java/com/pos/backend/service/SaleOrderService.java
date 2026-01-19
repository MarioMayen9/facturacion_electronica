package com.pos.backend.service;

import com.pos.backend.entity.*;
import com.pos.backend.dto.SaleOrderRequest;
import com.pos.backend.dto.SaleOrderDetailRequest;
import com.pos.backend.repository.*;
import com.pos.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class SaleOrderService {

    private static final Logger log = LoggerFactory.getLogger(SaleOrderService.class);

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private SaleOrderDetailRepository saleOrderDetailRepository;

    @Autowired
    private SalePointRepository salePointRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private SalePointDocumentTypeRepository salePointDocumentTypeRepository;

    @Autowired
    private PaymentFormRepository paymentFormRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    public List<SaleOrder> getAllSaleOrders() {
        log.info("Obteniendo todas las órdenes de venta con detalles");
        return saleOrderRepository.findAllWithDetails();
    }

    public Optional<SaleOrder> getSaleOrderById(Integer id) {
        log.info("Obteniendo orden de venta por ID con detalles: {}", id);
        return saleOrderRepository.findByIdWithDetails(id);
    }
    
    /**
     * Obtiene ventas filtradas por cliente
     */
    public List<SaleOrder> getSaleOrdersByClient(Integer clientId, Integer organizationId) {
        log.info("Obteniendo ventas por cliente con detalles: {} en organización: {}", clientId, organizationId);
        return saleOrderRepository.findByClientIdAndOrganizationIdWithDetails(clientId, organizationId);
    }
    
    /**
     * Obtiene ventas filtradas por punto de venta
     */
    public List<SaleOrder> getSaleOrdersBySalePoint(Integer salePointId, Integer organizationId) {
        log.info("Obteniendo ventas por punto de venta con detalles: {} en organización: {}", salePointId, organizationId);
        return saleOrderRepository.findBySalePointIdAndOrganizationIdWithDetails(salePointId, organizationId);
    }
    
    /**
     * Obtiene ventas filtradas por vendedor (usuario que las creó)
     */
    public List<SaleOrder> getSaleOrdersByVendedor(Integer createdBy, Integer organizationId) {
        log.info("Obteniendo ventas por vendedor con detalles: {} en organización: {}", createdBy, organizationId);
        return saleOrderRepository.findByCreatedByAndOrganizationIdWithDetails(createdBy, organizationId);
    }
    
    /**
     * Obtiene las ventas del usuario autenticado (extraído del token JWT)
     */
    public List<SaleOrder> getMySales(String userToken, Integer organizationId) {
        log.info("Obteniendo ventas del usuario autenticado con detalles en organización: {}", organizationId);
        
        // Extraer el ID del usuario desde el token
        Integer userId = extractUserIdFromToken(userToken);
        
        // Obtener las ventas creadas por este usuario
        return saleOrderRepository.findByCreatedByAndOrganizationIdWithDetails(userId, organizationId);
    }

    @Transactional
    public SaleOrder createSaleOrder(SaleOrderRequest request, String userToken) {
        log.info("=================== INICIO CREACIÓN DE VENTA ===================");
        log.info("Creando nueva orden de venta para cliente: {}, punto de venta: {}, tipo documento: {}", 
                  request.getClientId(), request.getSalePointId(), request.getDocumentTypeId());

        try {
            // 1. VALIDACIONES DE ENTIDADES RELACIONADAS
            validateRelatedEntities(request);

            // 2. VALIDACIÓN DE PERMISOS DE USUARIO EN PUNTO DE VENTA
            validateUserPermissions(request.getSalePointId(), userToken);

            // 3. OBTENER Y VALIDAR CONFIGURACIÓN CORRELATIVA
            SalePointDocumentType salePointDocType = validateAndGetCorrelativeConfig(
                request.getSalePointId(), request.getDocumentTypeId());

            // 4. VALIDAR INVENTARIO Y CALCULAR PRECIOS
            List<SaleOrderDetailCalculation> detailCalculations = validateInventoryAndCalculatePrices(request.getDetails());

            // 5. CREAR LA ORDEN PRINCIPAL
            SaleOrder saleOrder = createMainOrder(request, salePointDocType, userToken);

            // 6. CALCULAR Y ASIGNAR TOTALES DESDE DETALLES
            calculateAndSetTotals(saleOrder, detailCalculations);

            // 7. GUARDAR LA ORDEN PRINCIPAL
            SaleOrder savedOrder = saleOrderRepository.save(saleOrder);
            log.info("✓ Orden principal guardada con ID: {} y número de documento: {}", 
                     savedOrder.getId(), savedOrder.getDocumentNumber());

            // 8. CREAR Y GUARDAR DETALLES CON CÁLCULOS FISCALES
            createAndSaveDetails(savedOrder, request.getDetails(), detailCalculations);

            // 9. INCREMENTAR CORRELATIVO
            updateCorrelative(salePointDocType);

            log.info("✓ Venta creada exitosamente - ID: {}, Documento: {}, Total: ${}", 
                     savedOrder.getId(), savedOrder.getDocumentNumber(), savedOrder.getSalesTotal());
            log.info("=================== FIN CREACIÓN DE VENTA ===================");

            return savedOrder;

        } catch (Exception e) {
            log.error("✗ Error al crear orden de venta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear orden de venta: " + e.getMessage());
        }
    }

    /**
     * Valida que existan todas las entidades relacionadas
     */
    private void validateRelatedEntities(SaleOrderRequest request) {
        log.info("▶ Validando entidades relacionadas...");

        if (!clientRepository.existsById(request.getClientId())) {
            throw new IllegalArgumentException("Cliente no encontrado con ID: " + request.getClientId());
        }

        if (!salePointRepository.existsById(request.getSalePointId())) {
            throw new IllegalArgumentException("Punto de venta no encontrado con ID: " + request.getSalePointId());
        }

        if (!documentTypeRepository.existsById(request.getDocumentTypeId())) {
            throw new IllegalArgumentException("Tipo de documento no encontrado con ID: " + request.getDocumentTypeId());
        }

        if (!paymentFormRepository.existsById(request.getPaymentTermId())) {
            throw new IllegalArgumentException("Forma de pago no encontrada con ID: " + request.getPaymentTermId());
        }

        log.info("✓ Validación de entidades relacionadas completada");
    }

    /**
     * Valida que el usuario tenga permisos en el punto de venta
     */
    private void validateUserPermissions(Integer salePointId, String userToken) {
        log.info("▶ Validando permisos de usuario en punto de venta: {}", salePointId);
        
        // TODO: Implementar validación real con JWT token
        // Por ahora, simulamos que siempre tiene permisos
        Optional<SalePoint> salePoint = salePointRepository.findById(salePointId);
        if (salePoint.isPresent()) {
            log.info("✓ Usuario autorizado para punto de venta: {}", salePointId);
        } else {
            throw new IllegalArgumentException("Punto de venta no encontrado: " + salePointId);
        }
    }

    /**
     * Obtiene y valida la configuración correlativa, genera el siguiente número
     */
    private SalePointDocumentType validateAndGetCorrelativeConfig(Integer salePointId, Integer documentTypeId) {
        log.info("▶ Validando configuración correlativa para punto: {}, documento: {}", salePointId, documentTypeId);

        // Buscar configuración usando el repository existente
        Optional<SalePointDocumentType> configOpt = salePointDocumentTypeRepository
            .findBySalePointAndDocumentType(salePointId, documentTypeId, 1); // organizationId = 1

        if (!configOpt.isPresent()) {
            throw new IllegalArgumentException(
                "No existe configuración correlativa para punto de venta " + salePointId + 
                " y tipo de documento " + documentTypeId);
        }

        SalePointDocumentType config = configOpt.get();
        
        // Verificar que no hayamos excedido el rango usando finalNumberAuthorized
        if (config.getLatestNumberIssued() >= config.getFinalNumberAuthorized()) {
            throw new IllegalArgumentException(
                "Se ha agotado el rango de numeración. Último emitido: " + config.getLatestNumberIssued() + 
                ", Máximo permitido: " + config.getFinalNumberAuthorized());
        }

        log.info("✓ Configuración correlativa válida - Siguiente número: {}", config.getLatestNumberIssued() + 1);
        return config;
    }

    /**
     * Valida inventario disponible y calcula precios desde el catálogo
     */
    private List<SaleOrderDetailCalculation> validateInventoryAndCalculatePrices(List<SaleOrderDetailRequest> details) {
        log.info("▶ Validando inventario y calculando precios para {} artículos", details.size());
        
        List<SaleOrderDetailCalculation> calculations = new ArrayList<>();

        for (SaleOrderDetailRequest detail : details) {
            Optional<Article> articleOpt = articleRepository.findById(detail.getArticleId());
            if (!articleOpt.isPresent()) {
                throw new IllegalArgumentException("Artículo no encontrado con ID: " + detail.getArticleId());
            }

            Article article = articleOpt.get();

            // Por simplicidad, asumimos que siempre hay stock disponible
            log.info("Artículo encontrado: {} - {}", article.getId(), article.getName());

            // Usar precio del detalle si se proporciona, sino usar precio del artículo (retailPrice)
            BigDecimal unitPrice = detail.getPrice() != null ? detail.getPrice() : article.getRetailPrice();
            if (unitPrice == null) {
                throw new IllegalArgumentException("No se puede determinar el precio para el artículo: " + article.getName());
            }

            // Crear cálculo del detalle
            SaleOrderDetailCalculation calc = new SaleOrderDetailCalculation();
            calc.setArticle(article);
            calc.setQuantity(detail.getQuantity());
            calc.setUnitPrice(unitPrice);
            calc.setAlternativeName(detail.getAlternativeName());
            
            // Calcular montos fiscales
            calculateDetailAmounts(calc);
            
            calculations.add(calc);
            
            log.debug("Artículo {}: Cantidad={}, Precio=${}, Subtotal=${}", 
                     article.getName(), detail.getQuantity(), unitPrice, calc.getLineTotal());
        }

        log.info("✓ Validación de inventario y cálculo de precios completada");
        return calculations;
    }

    /**
     * Calcula montos fiscales para un detalle
     */
    private void calculateDetailAmounts(SaleOrderDetailCalculation calc) {
        BigDecimal lineTotal = calc.getQuantity().multiply(calc.getUnitPrice());
        calc.setLineTotal(lineTotal);

        // En El Salvador, precio ya incluye IVA del 13%
        // Necesitamos calcular el monto base (sin IVA) y el IVA
        BigDecimal ivaTaxRate = new BigDecimal("0.13");
        BigDecimal baseAmount = lineTotal.divide(BigDecimal.ONE.add(ivaTaxRate), 4, RoundingMode.HALF_UP);
        BigDecimal ivaAmount = lineTotal.subtract(baseAmount);

        // Todo va a gravado (subject_amount) por defecto
        calc.setSubjectAmount(baseAmount);
        calc.setExemptAmount(BigDecimal.ZERO);
        calc.setNotSubjectAmount(BigDecimal.ZERO);
        calc.setTaxAmount(ivaAmount);

        // Precios de referencia
        calc.setRetailPrice(calc.getUnitPrice());
        calc.setPrice(calc.getUnitPrice());
        calc.setPriceWithVat(calc.getUnitPrice());
        calc.setCost(BigDecimal.ZERO); // Por simplicidad
    }

    /**
     * Crea la orden principal con número de documento
     */
    private SaleOrder createMainOrder(SaleOrderRequest request, SalePointDocumentType salePointDocType, String userToken) {
        log.info("▶ Creando orden principal...");

        // Extraer usuario del token JWT
        Integer createdByUserId = extractUserIdFromToken(userToken);

        SaleOrder saleOrder = new SaleOrder();
        
        // Datos básicos requeridos
        saleOrder.setClientId(request.getClientId());
        saleOrder.setSalePointId(request.getSalePointId());
        saleOrder.setDocumentTypeId(request.getDocumentTypeId());
        saleOrder.setPaymentTermId(request.getPaymentTermId());
        saleOrder.setOrganizationId(request.getOrganizationId());
        saleOrder.setCreatedBy(createdByUserId); // Extraído del token JWT

        // Generar número de documento correlativo
        Integer nextDocumentNumber = salePointDocType.getLatestNumberIssued() + 1;
        saleOrder.setDocumentNumber(nextDocumentNumber);

        // Fechas y tiempos
        saleOrder.setEmissionDate(request.getEmissionDate() != null ? request.getEmissionDate() : LocalDate.now());
        saleOrder.setEmissionTime(request.getEmissionTime() != null ? request.getEmissionTime() : LocalTime.now());
        saleOrder.setRegistrationDate(LocalDate.now());
        
        // Estado y datos opcionales
        saleOrder.setStatus(request.getStatus() != null ? request.getStatus() : "E"); // E = Emitida
        saleOrder.setRemark(request.getRemark());

        // Campos adicionales opcionales
        saleOrder.setSalePointDocumentTypeId(salePointDocType.getId());
        saleOrder.setPaymentFormId(request.getPaymentFormId());
        saleOrder.setOperationType(request.getOperationType());
        saleOrder.setIncomeType(request.getIncomeType());

        log.info("✓ Orden principal creada con número de documento: {}", nextDocumentNumber);
        return saleOrder;
    }

    /**
     * Calcula y asigna los totales desde los detalles calculados
     */
    private void calculateAndSetTotals(SaleOrder saleOrder, List<SaleOrderDetailCalculation> detailCalculations) {
        log.info("▶ Calculando totales desde {} detalles", detailCalculations.size());

        BigDecimal subjectAmountSum = BigDecimal.ZERO;
        BigDecimal exemptAmountSum = BigDecimal.ZERO;
        BigDecimal notSubjectAmountSum = BigDecimal.ZERO;
        BigDecimal collectedTaxAmountSum = BigDecimal.ZERO;
        BigDecimal salesSubtotal = BigDecimal.ZERO;

        for (SaleOrderDetailCalculation calc : detailCalculations) {
            subjectAmountSum = subjectAmountSum.add(calc.getSubjectAmount());
            exemptAmountSum = exemptAmountSum.add(calc.getExemptAmount());
            notSubjectAmountSum = notSubjectAmountSum.add(calc.getNotSubjectAmount());
            collectedTaxAmountSum = collectedTaxAmountSum.add(calc.getTaxAmount());
            salesSubtotal = salesSubtotal.add(calc.getLineTotal());
        }

        BigDecimal salesTotal = salesSubtotal; // El lineTotal ya incluye IVA

        saleOrder.setSubjectAmountSum(subjectAmountSum);
        saleOrder.setExemptAmountSum(exemptAmountSum);
        saleOrder.setNotSubjectAmountSum(notSubjectAmountSum);
        saleOrder.setCollectedTaxAmountSum(collectedTaxAmountSum);
        saleOrder.setSalesTotal(salesTotal);

        log.info("✓ Totales calculados - Subtotal: ${}, IVA: ${}, Total: ${}", 
                 subjectAmountSum, collectedTaxAmountSum, salesTotal);
    }

    /**
     * Crea y guarda los detalles con cálculos fiscales
     */
    private void createAndSaveDetails(SaleOrder savedOrder, List<SaleOrderDetailRequest> detailRequests, 
                                    List<SaleOrderDetailCalculation> calculations) {
        log.info("▶ Creando y guardando {} detalles", calculations.size());

        List<SaleOrderDetail> details = new ArrayList<>();
        
        for (int i = 0; i < calculations.size(); i++) {
            SaleOrderDetailCalculation calc = calculations.get(i);
            SaleOrderDetailRequest request = detailRequests.get(i);

            SaleOrderDetail detail = new SaleOrderDetail();
            detail.setOrderId(savedOrder.getId()); // Usar setOrderId
            detail.setArticleId(calc.getArticle().getId());
            detail.setQuantity(calc.getQuantity());
            detail.setAlternativeName(calc.getAlternativeName());
            detail.setOrganizationId(savedOrder.getOrganizationId());

            // Montos fiscales calculados
            detail.setSubjectAmount(calc.getSubjectAmount());
            detail.setExemptAmount(calc.getExemptAmount());
            detail.setNotSubjectAmount(calc.getNotSubjectAmount());

            // Precios
            detail.setRetailPrice(calc.getRetailPrice());
            detail.setPrice(calc.getPrice());
            detail.setPriceWithVat(calc.getPriceWithVat());
            detail.setCost(calc.getCost());

            details.add(detail);
        }

        saleOrderDetailRepository.saveAll(details);
        log.info("✓ {} detalles guardados correctamente", details.size());
    }

    /**
     * Incrementa el correlativo del punto de venta
     */
    private void updateCorrelative(SalePointDocumentType salePointDocType) {
        log.info("▶ Incrementando correlativo desde {} a {}", 
                 salePointDocType.getLatestNumberIssued(), salePointDocType.getLatestNumberIssued() + 1);

        salePointDocType.setLatestNumberIssued(salePointDocType.getLatestNumberIssued() + 1);
        salePointDocumentTypeRepository.save(salePointDocType);

        log.info("✓ Correlativo actualizado a: {}", salePointDocType.getLatestNumberIssued());
    }

    public SaleOrder updateSaleOrder(Integer id, SaleOrderRequest request) {
        log.info("Actualizando orden de venta con ID: {}", id);
        
        Optional<SaleOrder> existingSaleOrder = saleOrderRepository.findById(id);
        if (existingSaleOrder.isPresent()) {
            SaleOrder saleOrder = existingSaleOrder.get();
            saleOrder.setClientId(request.getClientId());
            saleOrder.setSalePointId(request.getSalePointId());
            saleOrder.setDocumentTypeId(request.getDocumentTypeId());
            saleOrder.setPaymentTermId(request.getPaymentTermId());
            saleOrder.setOrganizationId(request.getOrganizationId());
            
            return saleOrderRepository.save(saleOrder);
        }
        
        throw new RuntimeException("Orden de venta no encontrada con ID: " + id);
    }

    public void deleteSaleOrder(Integer id) {
        log.info("Eliminando orden de venta con ID: {}", id);
        
        if (!saleOrderRepository.existsById(id)) {
            throw new RuntimeException("Orden de venta no encontrada con ID: " + id);
        }
        
        saleOrderRepository.deleteById(id);
    }

    /**
     * Clase interna para los cálculos de detalles
     */
    private static class SaleOrderDetailCalculation {
        private Article article;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private String alternativeName;
        private BigDecimal lineTotal;
        private BigDecimal subjectAmount;
        private BigDecimal exemptAmount;
        private BigDecimal notSubjectAmount;
        private BigDecimal taxAmount;
        private BigDecimal retailPrice;
        private BigDecimal price;
        private BigDecimal priceWithVat;
        private BigDecimal cost;

        // Getters y setters
        public Article getArticle() { return article; }
        public void setArticle(Article article) { this.article = article; }
        
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        
        public String getAlternativeName() { return alternativeName; }
        public void setAlternativeName(String alternativeName) { this.alternativeName = alternativeName; }
        
        public BigDecimal getLineTotal() { return lineTotal; }
        public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
        
        public BigDecimal getSubjectAmount() { return subjectAmount; }
        public void setSubjectAmount(BigDecimal subjectAmount) { this.subjectAmount = subjectAmount; }
        
        public BigDecimal getExemptAmount() { return exemptAmount; }
        public void setExemptAmount(BigDecimal exemptAmount) { this.exemptAmount = exemptAmount; }
        
        public BigDecimal getNotSubjectAmount() { return notSubjectAmount; }
        public void setNotSubjectAmount(BigDecimal notSubjectAmount) { this.notSubjectAmount = notSubjectAmount; }
        
        public BigDecimal getTaxAmount() { return taxAmount; }
        public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
        
        public BigDecimal getRetailPrice() { return retailPrice; }
        public void setRetailPrice(BigDecimal retailPrice) { this.retailPrice = retailPrice; }
        
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        
        public BigDecimal getPriceWithVat() { return priceWithVat; }
        public void setPriceWithVat(BigDecimal priceWithVat) { this.priceWithVat = priceWithVat; }
        
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
    }
    
    /**
     * Extrae el ID del usuario desde el token JWT
     */
    private Integer extractUserIdFromToken(String userToken) {
        if (userToken == null || userToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Token de usuario requerido para crear la venta");
        }
        
        try {
            // Extraer el email del token JWT
            String email = jwtUtil.getCorreoFromJWT(userToken);
            
            // Buscar el usuario por email
            Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + email));
            
            log.info("✓ Usuario extraído del token: {} (ID: {})", email, usuario.getId());
            return usuario.getId();
            
        } catch (Exception e) {
            log.error("✗ Error al extraer usuario del token: {}", e.getMessage());
            throw new IllegalArgumentException("Token de usuario inválido: " + e.getMessage());
        }
    }
}