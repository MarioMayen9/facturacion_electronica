package com.pos.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SALE_Order_Tax")
public class SaleOrderTax {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Order ID is required")
    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @NotNull(message = "Tax ID is required")
    @Column(name = "tax_id", nullable = false)
    private Integer taxId;

    @NotNull(message = "Own exempt amount total is required")
    @DecimalMin(value = "0.00", message = "Own exempt amount total cannot be negative")
    @Column(name = "own_exempt_amount_total", precision = 13, scale = 2, nullable = false)
    private BigDecimal ownExemptAmountTotal = BigDecimal.ZERO;

    @NotNull(message = "Own subject amount total is required")
    @DecimalMin(value = "0.00", message = "Own subject amount total cannot be negative")
    @Column(name = "own_subject_amount_total", precision = 13, scale = 2, nullable = false)
    private BigDecimal ownSubjectAmountTotal = BigDecimal.ZERO;

    @NotNull(message = "Own subject amount tax total is required")
    @DecimalMin(value = "0.00", message = "Own subject amount tax total cannot be negative")
    @Column(name = "own_subject_amount_tax_total", precision = 13, scale = 2, nullable = false)
    private BigDecimal ownSubjectAmountTaxTotal = BigDecimal.ZERO;

    @Column(name = "date")
    private LocalDate date;

    @Size(max = 255, message = "Serial number cannot exceed 255 characters")
    @Column(name = "serial_number", length = 255)
    private String serialNumber;

    @Size(max = 255, message = "Document number cannot exceed 255 characters")
    @Column(name = "document_number", length = 255)
    private String documentNumber;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @NotNull(message = "Organization ID is required")
    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public SaleOrderTax() {}

    public SaleOrderTax(Integer orderId, Integer taxId, Integer organizationId) {
        this.orderId = orderId;
        this.taxId = taxId;
        this.organizationId = organizationId;
        this.ownExemptAmountTotal = BigDecimal.ZERO;
        this.ownSubjectAmountTotal = BigDecimal.ZERO;
        this.ownSubjectAmountTaxTotal = BigDecimal.ZERO;
    }

    public SaleOrderTax(Integer orderId, Integer taxId, BigDecimal ownExemptAmountTotal, 
                       BigDecimal ownSubjectAmountTotal, BigDecimal ownSubjectAmountTaxTotal, 
                       Integer organizationId) {
        this.orderId = orderId;
        this.taxId = taxId;
        this.ownExemptAmountTotal = ownExemptAmountTotal != null ? ownExemptAmountTotal : BigDecimal.ZERO;
        this.ownSubjectAmountTotal = ownSubjectAmountTotal != null ? ownSubjectAmountTotal : BigDecimal.ZERO;
        this.ownSubjectAmountTaxTotal = ownSubjectAmountTaxTotal != null ? ownSubjectAmountTaxTotal : BigDecimal.ZERO;
        this.organizationId = organizationId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getTaxId() {
        return taxId;
    }

    public void setTaxId(Integer taxId) {
        this.taxId = taxId;
    }

    public BigDecimal getOwnExemptAmountTotal() {
        return ownExemptAmountTotal;
    }

    public void setOwnExemptAmountTotal(BigDecimal ownExemptAmountTotal) {
        this.ownExemptAmountTotal = ownExemptAmountTotal;
    }

    public BigDecimal getOwnSubjectAmountTotal() {
        return ownSubjectAmountTotal;
    }

    public void setOwnSubjectAmountTotal(BigDecimal ownSubjectAmountTotal) {
        this.ownSubjectAmountTotal = ownSubjectAmountTotal;
    }

    public BigDecimal getOwnSubjectAmountTaxTotal() {
        return ownSubjectAmountTaxTotal;
    }

    public void setOwnSubjectAmountTaxTotal(BigDecimal ownSubjectAmountTaxTotal) {
        this.ownSubjectAmountTaxTotal = ownSubjectAmountTaxTotal;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "SaleOrderTax{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", taxId=" + taxId +
                ", ownExemptAmountTotal=" + ownExemptAmountTotal +
                ", ownSubjectAmountTotal=" + ownSubjectAmountTotal +
                ", ownSubjectAmountTaxTotal=" + ownSubjectAmountTaxTotal +
                ", organizationId=" + organizationId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}