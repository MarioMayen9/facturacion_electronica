package com.pos.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class DocumentTypeRequest {
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @JsonProperty("name")
    private String name; // Nombre del documento
    
    @Size(max = 10, message = "Code cannot exceed 10 characters")
    @JsonProperty("code")
    private String code; // Código según normativa fiscal
    
    @JsonProperty("has_decrease_effect")
    private Boolean hasDecreaseEffect = false; // Disminuye valores (para Notas de Crédito)
    
    @JsonProperty("price_with_vat_is_visible")
    private Boolean priceWithVatIsVisible = false; // Mostrar precios con IVA
    
    @NotNull(message = "Organization ID is required")
    @JsonProperty("organization_id")
    private Integer organizationId;
    
    // Constructor
    public DocumentTypeRequest() {}
    
    public DocumentTypeRequest(String name, String code, Boolean hasDecreaseEffect, Boolean priceWithVatIsVisible, Integer organizationId) {
        this.name = name;
        this.code = code;
        this.hasDecreaseEffect = hasDecreaseEffect;
        this.priceWithVatIsVisible = priceWithVatIsVisible;
        this.organizationId = organizationId;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public Boolean getHasDecreaseEffect() {
        return hasDecreaseEffect;
    }
    
    public void setHasDecreaseEffect(Boolean hasDecreaseEffect) {
        this.hasDecreaseEffect = hasDecreaseEffect;
    }
    
    public Boolean getPriceWithVatIsVisible() {
        return priceWithVatIsVisible;
    }
    
    public void setPriceWithVatIsVisible(Boolean priceWithVatIsVisible) {
        this.priceWithVatIsVisible = priceWithVatIsVisible;
    }
    
    public Integer getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }
}