package com.pos.backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "taxpayer_classification_tax")
public class TaxpayerClassificationTax {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "organization_id", nullable = false)
    @JsonProperty("organization_id")
    private Integer organizationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxpayer_classification_id", nullable = false)
    @JsonProperty("taxpayer_classification_id")
    private TaxpayerClassification taxpayerClassification;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id", nullable = false)
    @JsonProperty("tax_id")
    private Tax tax;
    
    @Column(name = "porcentaje", precision = 5, scale = 2)
    private BigDecimal porcentaje;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    @Column(name = "created_at")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    public TaxpayerClassificationTax() {}
    
    public TaxpayerClassificationTax(Integer id, Integer organizationId, TaxpayerClassification taxpayerClassification, Tax tax, BigDecimal porcentaje, Boolean activo, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.organizationId = organizationId;
        this.taxpayerClassification = taxpayerClassification;
        this.tax = tax;
        this.porcentaje = porcentaje;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getOrganizationId() { return organizationId; }
    public void setOrganizationId(Integer organizationId) { this.organizationId = organizationId; }
    
    public TaxpayerClassification getTaxpayerClassification() { return taxpayerClassification; }
    public void setTaxpayerClassification(TaxpayerClassification taxpayerClassification) { this.taxpayerClassification = taxpayerClassification; }
    
    public Tax getTax() { return tax; }
    public void setTax(Tax tax) { this.tax = tax; }
    
    public BigDecimal getPorcentaje() { return porcentaje; }
    public void setPorcentaje(BigDecimal porcentaje) { this.porcentaje = porcentaje; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}