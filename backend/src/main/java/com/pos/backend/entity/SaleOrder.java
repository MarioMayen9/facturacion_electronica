package com.pos.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "SALE_Order")
public class SaleOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int unsigned")
    private Integer id;

    // Información del documento
    @Column(name = "document_number")
    private Integer documentNumber; // Correlativo del documento fiscal

    @Column(name = "emission_date")
    private LocalDate emissionDate; // Fecha de emisión

    @Column(name = "emission_time")
    private LocalTime emissionTime; // Hora de emisión

    @Column(name = "registration_date")
    private LocalDate registrationDate; // Fecha de registro contable

    @Column(name = "collection_date")
    private LocalDate collectionDate; // Fecha de cobro

    @Column(name = "reversal_date")
    private LocalDate reversalDate; // Fecha de anulación

    @NotNull
    @Column(name = "status", length = 1, nullable = false)
    private String status; // Estado (E: Emitida, A: Anulada)

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark; // Concepto o descripción de la venta

    // Montos y totales
    @NotNull
    @Column(name = "subject_amount_sum", precision = 13, scale = 2, nullable = false)
    private BigDecimal subjectAmountSum = BigDecimal.ZERO; // Suma de montos afectos

    @NotNull
    @Column(name = "exempt_amount_sum", precision = 13, scale = 2, nullable = false)
    private BigDecimal exemptAmountSum = BigDecimal.ZERO; // Suma de montos exentos

    @NotNull
    @Column(name = "not_subject_amount_sum", precision = 13, scale = 2, nullable = false)
    private BigDecimal notSubjectAmountSum = BigDecimal.ZERO; // Suma de montos no sujetos

    @NotNull
    @Column(name = "collected_tax_amount_sum", precision = 13, scale = 2, nullable = false)
    private BigDecimal collectedTaxAmountSum = BigDecimal.ZERO; // Suma de impuestos (IVA)

    @NotNull
    @Column(name = "withheld_tax_amount_sum", precision = 13, scale = 2, nullable = false)
    private BigDecimal withheldTaxAmountSum = BigDecimal.ZERO; // Suma de retenciones

    @NotNull
    @Column(name = "sales_total", precision = 13, scale = 2, nullable = false)
    private BigDecimal salesTotal = BigDecimal.ZERO; // Total de la venta sin impuestos

    // Foreign Keys
    @NotNull
    @Column(name = "client_id", columnDefinition = "int unsigned", nullable = false)
    private Integer clientId; // FK a SALE_Client

    @NotNull
    @Column(name = "payment_term_id", columnDefinition = "int unsigned", nullable = false)
    private Integer paymentTermId; // FK a PURCH_Payment_Term

    @Column(name = "payment_form_id", columnDefinition = "int unsigned")
    private Integer paymentFormId; // FK a PURCH_Payment_Form

    @Column(name = "document_type_id", columnDefinition = "int unsigned")
    private Integer documentTypeId; // FK a PURCH_Document_Type

    @Column(name = "sale_point_id", columnDefinition = "int unsigned")
    private Integer salePointId; // FK a SALE_Sale_Point

    @Column(name = "sale_point_document_type_id", columnDefinition = "int unsigned")
    private Integer salePointDocumentTypeId; // FK a SALE_Sale_Point_Document_Type

    @NotNull
    @Column(name = "created_by", columnDefinition = "int unsigned", nullable = false)
    private Integer createdBy; // Usuario que creó la venta

    // Campos para declaración de impuestos
    @Column(name = "operation_type", length = 2)
    private String operationType; 
    /*
    1: Gravada
    2: No Gravada o Exento
    3: Excluido o no constituye renta
    4: Mixta (gravada y exenta)
    12: Ingresos que ya fueron sujetos de retención...
    13: Sujetos pasivos excluidos...
    */

    @Column(name = "income_type", length = 2)
    private String incomeType;
    /*
    1: Profesiones, Artes y Oficios
    2: Actividades de Servicios
    3: Actividades Comerciales
    4: Actividades Industriales
    5: Actividades Agropecuarias
    6: Utilidades y Dividendos
    7: Exportaciones de bienes
    8: Servicios Realizados en el Exterior y Utilizados en El Salvador
    9: Exportaciones de servicios
    10: Otras Rentas Gravables
    12: Ingresos que ya fueron sujetos de retención...
    13: Sujetos pasivos excluidos...
    */

    // Campos para DTE (Documento Tributario Electrónico)
    @Column(name = "transmission_type", length = 1)
    private String transmissionType; // Tipo de transmisión

    @Column(name = "control_number", length = 255)
    private String controlNumber; // Número de control DTE

    @Column(name = "issue_generation_code", length = 255)
    private String issueGenerationCode; // Código de generación DTE

    @Column(name = "void_generation_code", length = 255)
    private String voidGenerationCode; // Código de anulación DTE

    @Column(name = "is_dte_processing")
    private Boolean isDteProcessing = false; // Indicador de procesamiento DTE

    @Column(name = "electronic_signature", columnDefinition = "TEXT")
    private String electronicSignature; // Firma electrónica

    @Column(name = "issue_received_stamp", columnDefinition = "TEXT")
    private String issueReceivedStamp; // Sello de recepción (emisión)

    @Column(name = "electronic_invoice_url", columnDefinition = "TEXT")
    private String electronicInvoiceUrl; // URL del DTE

    @Column(name = "void_received_stamp", columnDefinition = "TEXT")
    private String voidReceivedStamp; // Sello de recepción (anulación)

    @Column(name = "electronic_invoice_json", columnDefinition = "TEXT")
    private String electronicInvoiceJson; // JSON del DTE

    @Column(name = "electronic_invoice_issue_response", columnDefinition = "TEXT")
    private String electronicInvoiceIssueResponse; // Respuesta de la API (emisión)

    @Column(name = "electronic_invoice_void_response", columnDefinition = "TEXT")
    private String electronicInvoiceVoidResponse; // Respuesta de la API (anulación)

    // Campos para exportación
    @Column(name = "export_item_type", length = 20)
    private String exportItemType;
    /*
    D: Bienes
    S: Servicios
    X: Ambos
    Y: Otros tributos por item
    */

    @Column(name = "exemption_type", length = 1)
    private String exemptionType;
    /*
    E: Exentas
    N: Exentas no sujetas a proporcionalidad
    */

    @Column(name = "export_type", length = 1)
    private String exportType;
    /*
    B: Bienes fuera de la región centroamericana
    C: Bienes dentro de la región centroamericana
    S: Servicios
    Z: Zona francas y DPA (tasa cero)
    */

    @Column(name = "export_item_type_code", length = 10)
    private String exportItemTypeCode;

    @Column(name = "fiscal_enclosure_code", length = 20)
    private String fiscalEnclosureCode;

    @Column(name = "fiscal_enclosure_name", length = 20)
    private String fiscalEnclosureName;

    @Column(name = "export_regime_code", length = 20)
    private String exportRegimeCode;

    @Column(name = "export_regime_name", length = 256)
    private String exportRegimeName;

    @Column(name = "incoterm_code", length = 20)
    private String incotermCode;

    @Column(name = "incoterm_name", length = 20)
    private String incotermName;

    @Column(name = "goods_dispatch_title", length = 255)
    private String goodsDispatchTitle;

    // Campos de información adicional
    @Column(name = "local", length = 255)
    private String local;

    @Column(name = "carrier", length = 255)
    private String carrier;

    @Column(name = "site", length = 255)
    private String site;

    @Column(name = "shipments", length = 255)
    private String shipments;

    @Column(name = "other", length = 255)
    private String other;

    @NotNull
    @Column(name = "organization_id", columnDefinition = "int unsigned", nullable = false)
    private Integer organizationId;

    // Relación con los detalles de la orden
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<SaleOrderDetail> details = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public SaleOrder() {}

    public SaleOrder(Integer clientId, Integer paymentTermId, Integer createdBy, Integer organizationId) {
        this.clientId = clientId;
        this.paymentTermId = paymentTermId;
        this.createdBy = createdBy;
        this.organizationId = organizationId;
        this.status = "E"; // Estado por defecto: Emitida
        this.subjectAmountSum = BigDecimal.ZERO;
        this.exemptAmountSum = BigDecimal.ZERO;
        this.notSubjectAmountSum = BigDecimal.ZERO;
        this.collectedTaxAmountSum = BigDecimal.ZERO;
        this.withheldTaxAmountSum = BigDecimal.ZERO;
        this.salesTotal = BigDecimal.ZERO;
        this.isDteProcessing = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (emissionDate == null) {
            emissionDate = LocalDate.now();
        }
        if (emissionTime == null) {
            emissionTime = LocalTime.now();
        }
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(Integer documentNumber) { this.documentNumber = documentNumber; }

    public LocalDate getEmissionDate() { return emissionDate; }
    public void setEmissionDate(LocalDate emissionDate) { this.emissionDate = emissionDate; }

    public LocalTime getEmissionTime() { return emissionTime; }
    public void setEmissionTime(LocalTime emissionTime) { this.emissionTime = emissionTime; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }

    public LocalDate getReversalDate() { return reversalDate; }
    public void setReversalDate(LocalDate reversalDate) { this.reversalDate = reversalDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public BigDecimal getSubjectAmountSum() { return subjectAmountSum; }
    public void setSubjectAmountSum(BigDecimal subjectAmountSum) { this.subjectAmountSum = subjectAmountSum; }

    public BigDecimal getExemptAmountSum() { return exemptAmountSum; }
    public void setExemptAmountSum(BigDecimal exemptAmountSum) { this.exemptAmountSum = exemptAmountSum; }

    public BigDecimal getNotSubjectAmountSum() { return notSubjectAmountSum; }
    public void setNotSubjectAmountSum(BigDecimal notSubjectAmountSum) { this.notSubjectAmountSum = notSubjectAmountSum; }

    public BigDecimal getCollectedTaxAmountSum() { return collectedTaxAmountSum; }
    public void setCollectedTaxAmountSum(BigDecimal collectedTaxAmountSum) { this.collectedTaxAmountSum = collectedTaxAmountSum; }

    public BigDecimal getWithheldTaxAmountSum() { return withheldTaxAmountSum; }
    public void setWithheldTaxAmountSum(BigDecimal withheldTaxAmountSum) { this.withheldTaxAmountSum = withheldTaxAmountSum; }

    public BigDecimal getSalesTotal() { return salesTotal; }
    public void setSalesTotal(BigDecimal salesTotal) { this.salesTotal = salesTotal; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Integer getPaymentTermId() { return paymentTermId; }
    public void setPaymentTermId(Integer paymentTermId) { this.paymentTermId = paymentTermId; }

    public Integer getPaymentFormId() { return paymentFormId; }
    public void setPaymentFormId(Integer paymentFormId) { this.paymentFormId = paymentFormId; }

    public Integer getDocumentTypeId() { return documentTypeId; }
    public void setDocumentTypeId(Integer documentTypeId) { this.documentTypeId = documentTypeId; }

    public Integer getSalePointId() { return salePointId; }
    public void setSalePointId(Integer salePointId) { this.salePointId = salePointId; }

    public Integer getSalePointDocumentTypeId() { return salePointDocumentTypeId; }
    public void setSalePointDocumentTypeId(Integer salePointDocumentTypeId) { this.salePointDocumentTypeId = salePointDocumentTypeId; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getIncomeType() { return incomeType; }
    public void setIncomeType(String incomeType) { this.incomeType = incomeType; }

    public String getTransmissionType() { return transmissionType; }
    public void setTransmissionType(String transmissionType) { this.transmissionType = transmissionType; }

    public String getControlNumber() { return controlNumber; }
    public void setControlNumber(String controlNumber) { this.controlNumber = controlNumber; }

    public String getIssueGenerationCode() { return issueGenerationCode; }
    public void setIssueGenerationCode(String issueGenerationCode) { this.issueGenerationCode = issueGenerationCode; }

    public String getVoidGenerationCode() { return voidGenerationCode; }
    public void setVoidGenerationCode(String voidGenerationCode) { this.voidGenerationCode = voidGenerationCode; }

    public Boolean getIsDteProcessing() { return isDteProcessing; }
    public void setIsDteProcessing(Boolean isDteProcessing) { this.isDteProcessing = isDteProcessing; }

    public String getElectronicSignature() { return electronicSignature; }
    public void setElectronicSignature(String electronicSignature) { this.electronicSignature = electronicSignature; }

    public String getIssueReceivedStamp() { return issueReceivedStamp; }
    public void setIssueReceivedStamp(String issueReceivedStamp) { this.issueReceivedStamp = issueReceivedStamp; }

    public String getElectronicInvoiceUrl() { return electronicInvoiceUrl; }
    public void setElectronicInvoiceUrl(String electronicInvoiceUrl) { this.electronicInvoiceUrl = electronicInvoiceUrl; }

    public String getVoidReceivedStamp() { return voidReceivedStamp; }
    public void setVoidReceivedStamp(String voidReceivedStamp) { this.voidReceivedStamp = voidReceivedStamp; }

    public String getElectronicInvoiceJson() { return electronicInvoiceJson; }
    public void setElectronicInvoiceJson(String electronicInvoiceJson) { this.electronicInvoiceJson = electronicInvoiceJson; }

    public String getElectronicInvoiceIssueResponse() { return electronicInvoiceIssueResponse; }
    public void setElectronicInvoiceIssueResponse(String electronicInvoiceIssueResponse) { this.electronicInvoiceIssueResponse = electronicInvoiceIssueResponse; }

    public String getElectronicInvoiceVoidResponse() { return electronicInvoiceVoidResponse; }
    public void setElectronicInvoiceVoidResponse(String electronicInvoiceVoidResponse) { this.electronicInvoiceVoidResponse = electronicInvoiceVoidResponse; }

    public String getExportItemType() { return exportItemType; }
    public void setExportItemType(String exportItemType) { this.exportItemType = exportItemType; }

    public String getExemptionType() { return exemptionType; }
    public void setExemptionType(String exemptionType) { this.exemptionType = exemptionType; }

    public String getExportType() { return exportType; }
    public void setExportType(String exportType) { this.exportType = exportType; }

    public String getExportItemTypeCode() { return exportItemTypeCode; }
    public void setExportItemTypeCode(String exportItemTypeCode) { this.exportItemTypeCode = exportItemTypeCode; }

    public String getFiscalEnclosureCode() { return fiscalEnclosureCode; }
    public void setFiscalEnclosureCode(String fiscalEnclosureCode) { this.fiscalEnclosureCode = fiscalEnclosureCode; }

    public String getFiscalEnclosureName() { return fiscalEnclosureName; }
    public void setFiscalEnclosureName(String fiscalEnclosureName) { this.fiscalEnclosureName = fiscalEnclosureName; }

    public String getExportRegimeCode() { return exportRegimeCode; }
    public void setExportRegimeCode(String exportRegimeCode) { this.exportRegimeCode = exportRegimeCode; }

    public String getExportRegimeName() { return exportRegimeName; }
    public void setExportRegimeName(String exportRegimeName) { this.exportRegimeName = exportRegimeName; }

    public String getIncotermCode() { return incotermCode; }
    public void setIncotermCode(String incotermCode) { this.incotermCode = incotermCode; }

    public String getIncotermName() { return incotermName; }
    public void setIncotermName(String incotermName) { this.incotermName = incotermName; }

    public String getGoodsDispatchTitle() { return goodsDispatchTitle; }
    public void setGoodsDispatchTitle(String goodsDispatchTitle) { this.goodsDispatchTitle = goodsDispatchTitle; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getSite() { return site; }
    public void setSite(String site) { this.site = site; }

    public String getShipments() { return shipments; }
    public void setShipments(String shipments) { this.shipments = shipments; }

    public String getOther() { return other; }
    public void setOther(String other) { this.other = other; }

    public Integer getOrganizationId() { return organizationId; }
    public void setOrganizationId(Integer organizationId) { this.organizationId = organizationId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<SaleOrderDetail> getDetails() { return details; }
    public void setDetails(List<SaleOrderDetail> details) { this.details = details; }

    @Override
    public String toString() {
        return "SaleOrder{" +
                "id=" + id +
                ", documentNumber=" + documentNumber +
                ", emissionDate=" + emissionDate +
                ", clientId=" + clientId +
                ", salesTotal=" + salesTotal +
                ", status='" + status + '\'' +
                '}';
    }

    // Métodos helper para calcular totales
    public void calculateTotals() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(subjectAmountSum);
        total = total.add(exemptAmountSum);
        total = total.add(notSubjectAmountSum);
        total = total.add(collectedTaxAmountSum);
        total = total.subtract(withheldTaxAmountSum);
        
        this.salesTotal = total;
        this.updatedAt = LocalDateTime.now();
    }
}