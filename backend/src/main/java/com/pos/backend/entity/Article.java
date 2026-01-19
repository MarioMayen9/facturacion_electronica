package com.pos.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "INV_Article")
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @Column(name = "sku", length = 255)
    private String sku; // Código de Artículo (SKU)

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name; // Nombre del Artículo

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Descripción

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl; // URL de la imagen

    @NotNull(message = "Retail price is required")
    @DecimalMin(value = "0.00", message = "Retail price must be greater than or equal to 0")
    @Column(name = "retail_price", precision = 13, scale = 2, nullable = false, columnDefinition = "DECIMAL(13,2) DEFAULT 0.00")
    private BigDecimal retailPrice = BigDecimal.ZERO; // Precio de Venta (con IVA)

    @Column(name = "is_vat_exempt", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isVatExempt = false; // Indicador si está exento de IVA

    @NotNull(message = "Organization ID is required")
    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    // Constructors
    public Article() {}

    public Article(String sku, String name, BigDecimal retailPrice, Integer organizationId) {
        this.sku = sku;
        this.name = name;
        this.retailPrice = retailPrice;
        this.organizationId = organizationId;
        this.isVatExempt = false;
    }

    @PrePersist
    protected void onCreate() {
        if (retailPrice == null) {
            retailPrice = BigDecimal.ZERO;
        }
        if (isVatExempt == null) {
            isVatExempt = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Mantener valores por defecto si son null
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Boolean getIsVatExempt() {
        return isVatExempt;
    }

    public void setIsVatExempt(Boolean isVatExempt) {
        this.isVatExempt = isVatExempt;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", retailPrice=" + retailPrice +
                ", isVatExempt=" + isVatExempt +
                '}';
    }
}