package com.pos.backend.controller;

import com.pos.backend.entity.SaleOrder;
import com.pos.backend.service.SaleOrderService;
import com.pos.backend.dto.SaleOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestión de órdenes de venta con compliance fiscal completo
 * 
 * Funcionalidades:
 * - Crear venta con validación de inventario, permisos y generación correlativa
 * - Cálculos automáticos de montos fiscales
 * - Gestión de inventario integrada
 * - Control de correlativos por punto de venta y tipo de documento
 */
@RestController
@RequestMapping("/api/sale-orders")
@CrossOrigin(origins = "*")
public class SaleOrderController {

    private static final Logger log = LoggerFactory.getLogger(SaleOrderController.class);

    @Autowired
    private SaleOrderService saleOrderService;

    /**
     * Obtiene todas las órdenes de venta
     */
    @GetMapping
    public ResponseEntity<List<SaleOrder>> getAllSaleOrders() {
        log.info("GET /api/sale-orders - Obteniendo todas las órdenes de venta");
        try {
            List<SaleOrder> saleOrders = saleOrderService.getAllSaleOrders();
            log.info("✓ {} órdenes de venta encontradas", saleOrders.size());
            return ResponseEntity.ok(saleOrders);
        } catch (Exception e) {
            log.error("✗ Error al obtener órdenes de venta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene una orden de venta por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleOrder> getSaleOrderById(@PathVariable Integer id) {
        log.info("GET /api/sale-orders/{} - Obteniendo orden de venta", id);
        try {
            Optional<SaleOrder> saleOrder = saleOrderService.getSaleOrderById(id);
            if (saleOrder.isPresent()) {
                log.info("✓ Orden de venta encontrada: {}", id);
                return ResponseEntity.ok(saleOrder.get());
            } else {
                log.warn("⚠ Orden de venta no encontrada: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("✗ Error al obtener orden de venta {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea una nueva orden de venta con validaciones completas y gestión fiscal
     * 
     * Proceso completo:
     * 1. Validación de entidades relacionadas
     * 2. Validación de permisos de usuario en punto de venta
     * 3. Validación y generación correlativa
     * 4. Validación de inventario disponible
     * 5. Cálculo automático de precios y montos fiscales
     * 6. Actualización de inventario
     * 7. Incremento de correlativo
     * 
     * @param request Datos de la venta incluyendo header y detalles
     * @param authorization Token JWT para validación de permisos
     * @return Orden de venta creada con todos los datos fiscales
     */
    @PostMapping
    public ResponseEntity<?> createSaleOrder(@Valid @RequestBody SaleOrderRequest request,
                                           @RequestHeader(value = "Authorization", required = false) String authorization) {
        log.info("=================== INICIO API CREAR VENTA ===================");
        log.info("POST /api/sale-orders - Cliente: {}, Punto Venta: {}, Documento: {}", 
                 request.getClientId(), request.getSalePointId(), request.getDocumentTypeId());
        
        try {
            // Extraer token JWT si existe
            String userToken = null;
            if (authorization != null && authorization.startsWith("Bearer ")) {
                userToken = authorization.substring(7);
            }

            // Crear la orden de venta con todas las validaciones
            SaleOrder createdSaleOrder = saleOrderService.createSaleOrder(request, userToken);

            // Respuesta exitosa con datos completos
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Venta creada exitosamente");
            response.put("data", createdSaleOrder);
            response.put("document_number", createdSaleOrder.getDocumentNumber());
            response.put("total", createdSaleOrder.getSalesTotal());

            log.info("✓ API Crear Venta exitosa - ID: {}, Documento: {}, Total: ${}", 
                     createdSaleOrder.getId(), createdSaleOrder.getDocumentNumber(), createdSaleOrder.getSalesTotal());
            log.info("=================== FIN API CREAR VENTA ===================");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("⚠ Error de validación en crear venta: {}", e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "VALIDATION_ERROR");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            log.error("✗ Error interno al crear venta: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "INTERNAL_ERROR");
            errorResponse.put("message", "Error interno del servidor al procesar la venta");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Actualiza una orden de venta existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<SaleOrder> updateSaleOrder(@PathVariable Integer id, 
                                                    @Valid @RequestBody SaleOrderRequest request) {
        log.info("PUT /api/sale-orders/{} - Actualizando orden de venta", id);
        try {
            SaleOrder updatedSaleOrder = saleOrderService.updateSaleOrder(id, request);
            log.info("✓ Orden de venta actualizada: {}", id);
            return ResponseEntity.ok(updatedSaleOrder);
        } catch (RuntimeException e) {
            log.warn("⚠ Error al actualizar orden de venta {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("✗ Error interno al actualizar orden de venta {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene ventas filtradas por cliente
     * 
     * @param clientId ID del cliente
     * @param organizationId ID de la organización
     * @return Lista de ventas del cliente
     */
    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<List<SaleOrder>> getSaleOrdersByClient(
            @PathVariable Integer clientId,
            @RequestParam Integer organizationId) {
        log.info("GET /api/sale-orders/by-client/{} - Obteniendo ventas por cliente", clientId);
        try {
            List<SaleOrder> saleOrders = saleOrderService.getSaleOrdersByClient(clientId, organizationId);
            log.info("✓ {} ventas encontradas para cliente: {}", saleOrders.size(), clientId);
            return ResponseEntity.ok(saleOrders);
        } catch (Exception e) {
            log.error("✗ Error al obtener ventas por cliente {}: {}", clientId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene ventas filtradas por punto de venta
     * 
     * @param salePointId ID del punto de venta
     * @param organizationId ID de la organización
     * @return Lista de ventas del punto de venta
     */
    @GetMapping("/by-sale-point/{salePointId}")
    public ResponseEntity<List<SaleOrder>> getSaleOrdersBySalePoint(
            @PathVariable Integer salePointId,
            @RequestParam Integer organizationId) {
        log.info("GET /api/sale-orders/by-sale-point/{} - Obteniendo ventas por punto de venta", salePointId);
        try {
            List<SaleOrder> saleOrders = saleOrderService.getSaleOrdersBySalePoint(salePointId, organizationId);
            log.info("✓ {} ventas encontradas para punto de venta: {}", saleOrders.size(), salePointId);
            return ResponseEntity.ok(saleOrders);
        } catch (Exception e) {
            log.error("✗ Error al obtener ventas por punto de venta {}: {}", salePointId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene ventas filtradas por vendedor (usuario que las creó)
     * 
     * @param vendedorId ID del vendedor (created_by)
     * @param organizationId ID de la organización
     * @return Lista de ventas del vendedor
     */
    @GetMapping("/by-vendedor/{vendedorId}")
    public ResponseEntity<List<SaleOrder>> getSaleOrdersByVendedor(
            @PathVariable Integer vendedorId,
            @RequestParam Integer organizationId) {
        log.info("GET /api/sale-orders/by-vendedor/{} - Obteniendo ventas por vendedor", vendedorId);
        try {
            List<SaleOrder> saleOrders = saleOrderService.getSaleOrdersByVendedor(vendedorId, organizationId);
            log.info("✓ {} ventas encontradas para vendedor: {}", saleOrders.size(), vendedorId);
            return ResponseEntity.ok(saleOrders);
        } catch (Exception e) {
            log.error("✗ Error al obtener ventas por vendedor {}: {}", vendedorId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene las ventas del usuario autenticado (extraído del token JWT)
     * 
     * @param authorization Token JWT del usuario
     * @param organizationId ID de la organización
     * @return Lista de ventas del usuario autenticado
     */
    @GetMapping("/my-sales")
    public ResponseEntity<List<SaleOrder>> getMySales(
            @RequestHeader(value = "Authorization", required = true) String authorization,
            @RequestParam Integer organizationId) {
        log.info("GET /api/sale-orders/my-sales - Obteniendo mis ventas");
        try {
            // Extraer token JWT
            String userToken = null;
            if (authorization != null && authorization.startsWith("Bearer ")) {
                userToken = authorization.substring(7);
            } else {
                log.warn("⚠ Token JWT no válido o faltante");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<SaleOrder> saleOrders = saleOrderService.getMySales(userToken, organizationId);
            log.info("✓ {} ventas encontradas para el usuario autenticado", saleOrders.size());
            return ResponseEntity.ok(saleOrders);
        } catch (Exception e) {
            log.error("✗ Error al obtener mis ventas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Elimina una orden de venta
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleOrder(@PathVariable Integer id) {
        log.info("DELETE /api/sale-orders/{} - Eliminando orden de venta", id);
        try {
            saleOrderService.deleteSaleOrder(id);
            log.info("✓ Orden de venta eliminada: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("⚠ Error al eliminar orden de venta {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("✗ Error interno al eliminar orden de venta {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}