package com.pos.backend.dto;

import jakarta.validation.constraints.*;

public class SalePointRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name; // Nombre del Punto de Venta

    @Size(max = 255, message = "User IDs must be at most 255 characters")
    private String userIds; // IDs de usuarios con acceso (separados por comas)

    @NotNull(message = "Organization ID is required")
    private Integer organizationId;

    // Constructors
    public SalePointRequest() {}

    public SalePointRequest(String name, String userIds, Integer organizationId) {
        this.name = name;
        this.userIds = userIds;
        this.organizationId = organizationId;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return "SalePointRequest{" +
                "name='" + name + '\'' +
                ", userIds='" + userIds + '\'' +
                ", organizationId=" + organizationId +
                '}';
    }
}