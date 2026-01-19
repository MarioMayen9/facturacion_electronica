package com.pos.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PURCH_Document_Type")
public class DocumentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name; // Nombre del documento
    
    @Column(name = "code", length = 10)
    private String code; // Código según normativa fiscal
    
    @Column(name = "has_decrease_effect", nullable = false)
    private Boolean hasDecreaseEffect = false; // Disminuye valores (para Notas de Crédito)
    
    @Column(name = "price_with_vat_is_visible")
    private Boolean priceWithVatIsVisible = false; // Mostrar precios con IVA
    
    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;
    
    // Constructors
    public DocumentType() {}
    
    public DocumentType(String name, String code, Boolean hasDecreaseEffect, Boolean priceWithVatIsVisible, Integer organizationId) {
        this.name = name;
        this.code = code;
        this.hasDecreaseEffect = hasDecreaseEffect;
        this.priceWithVatIsVisible = priceWithVatIsVisible;
        this.organizationId = organizationId;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
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