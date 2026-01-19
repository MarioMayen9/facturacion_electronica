package com.pos.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ArticleRequest {

    @Size(max = 255, message = "SKU must not exceed 255 characters")
    private String sku; // Código de Artículo (SKU)

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name; // Nombre del Artículo

    private String description; // Descripción

    private String imageUrl; // URL de la imagen

    @NotNull(message = "Retail price is required")
    @DecimalMin(value = "0.00", message = "Retail price must be greater than or equal to 0")
    private BigDecimal retailPrice; // Precio de Venta (con IVA)

    private Boolean isVatExempt = false; // Indicador si está exento de IVA

    @NotNull(message = "Organization ID is required")
    private Integer organizationId;

    // Constructors
    public ArticleRequest() {}

    // Getters and Setters
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
}