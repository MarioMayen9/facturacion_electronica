package com.pos.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Request DTO for creating sale orders with all fiscal compliance fields
 */
public class SaleOrderRequest {
    
    // Campos obligatorios simplificados
    @NotNull(message = "El ID del cliente es obligatorio")
    @JsonProperty("client_id")
    private Integer clientId;

    @NotNull(message = "El ID del punto de venta es obligatorio")
    @JsonProperty("sale_point_id")
    private Integer salePointId;

    @NotNull(message = "El ID del tipo de documento es obligatorio")
    @JsonProperty("document_type_id")
    private Integer documentTypeId;

    @NotNull(message = "El ID de la forma de pago es obligatorio")
    @JsonProperty("payment_term_id")
    private Integer paymentTermId;

    @NotNull(message = "El ID de organización es obligatorio")
    @JsonProperty("organization_id")
    private Integer organizationId;

    @NotNull(message = "Los detalles son obligatorios")
    @Valid
    @JsonProperty("details")
    private List<SaleOrderDetailRequest> details;

    // Campos opcionales adicionales para flexibilidad
    @JsonProperty("document_number")
    private Integer documentNumber;

    @JsonProperty("emission_date")
    private LocalDate emissionDate;

    @JsonProperty("emission_time")
    private LocalTime emissionTime;

    @JsonProperty("registration_date")
    private LocalDate registrationDate;

    @JsonProperty("collection_date")
    private LocalDate collectionDate;

    @Size(max = 100, message = "El estado debe ser máximo 100 caracteres")
    @JsonProperty("status")
    private String status;

    @Size(max = 255, message = "La observación debe ser máximo 255 caracteres")
    @JsonProperty("remark")
    private String remark;

    // Montos calculados automáticamente
    @JsonProperty("subject_amount_sum")
    private BigDecimal subjectAmountSum;

    @JsonProperty("exempt_amount_sum")
    private BigDecimal exemptAmountSum;

    @JsonProperty("not_subject_amount_sum")
    private BigDecimal notSubjectAmountSum;

    @JsonProperty("collected_tax_amount_sum")
    private BigDecimal collectedTaxAmountSum;

    @JsonProperty("withheld_tax_amount_sum")
    private BigDecimal withheldTaxAmountSum;

    @JsonProperty("sales_total")
    private BigDecimal salesTotal;

    // Campos adicionales opcionales
    @JsonProperty("payment_form_id")
    private Integer paymentFormId;

    @JsonProperty("sale_point_document_type_id")
    private Integer salePointDocumentTypeId;

    @Size(max = 256, message = "El tipo de operación debe ser máximo 256 caracteres")
    @JsonProperty("operation_type")
    private String operationType;

    @Size(max = 20, message = "El tipo de entrada debe ser máximo 20 caracteres")
    @JsonProperty("income_type")
    private String incomeType;

    // Constructor vacío
    public SaleOrderRequest() {}

    // Getters y Setters principales
    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Integer getSalePointId() { return salePointId; }
    public void setSalePointId(Integer salePointId) { this.salePointId = salePointId; }

    public Integer getDocumentTypeId() { return documentTypeId; }
    public void setDocumentTypeId(Integer documentTypeId) { this.documentTypeId = documentTypeId; }

    public Integer getPaymentTermId() { return paymentTermId; }
    public void setPaymentTermId(Integer paymentTermId) { this.paymentTermId = paymentTermId; }

    public Integer getOrganizationId() { return organizationId; }
    public void setOrganizationId(Integer organizationId) { this.organizationId = organizationId; }

    public List<SaleOrderDetailRequest> getDetails() { return details; }
    public void setDetails(List<SaleOrderDetailRequest> details) { this.details = details; }

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

    public Integer getPaymentFormId() { return paymentFormId; }
    public void setPaymentFormId(Integer paymentFormId) { this.paymentFormId = paymentFormId; }

    public Integer getSalePointDocumentTypeId() { return salePointDocumentTypeId; }
    public void setSalePointDocumentTypeId(Integer salePointDocumentTypeId) { this.salePointDocumentTypeId = salePointDocumentTypeId; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getIncomeType() { return incomeType; }
    public void setIncomeType(String incomeType) { this.incomeType = incomeType; }
}