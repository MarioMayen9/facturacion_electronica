package com.pos.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "SALE_Sale_Point")
public class SalePoint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name; // Nombre del Punto de Venta

    @Size(max = 255)
    @Column(name = "user_ids", length = 255)
    private String userIds; // IDs de usuarios con acceso (separados por comas)

    @NotNull(message = "Organization ID is required")
    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    // Constructors
    public SalePoint() {}

    public SalePoint(String name, String userIds, Integer organizationId) {
        this.name = name;
        this.userIds = userIds;
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
        return "SalePoint{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userIds='" + userIds + '\'' +
                ", organizationId=" + organizationId +
                '}';
    }
}