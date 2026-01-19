package com.pos.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PaymentTermRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private Integer days;

    @NotNull(message = "IsCash field is required")
    private Boolean isCash;

    @NotNull(message = "Organization ID is required")
    private Integer organizationId;

    // Constructors
    public PaymentTermRequest() {}

    public PaymentTermRequest(String name, String description, Integer days, Boolean isCash, Integer organizationId) {
        this.name = name;
        this.description = description;
        this.days = days;
        this.isCash = isCash;
        this.organizationId = organizationId;
    }

    // Getters and Setters
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

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Boolean getIsCash() {
        return isCash;
    }

    public void setIsCash(Boolean isCash) {
        this.isCash = isCash;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }
}