package com.pos.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "purch_payment_form")
public class PaymentForm {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "bank_account_is_requested")
    private Boolean bankAccountIsRequested = false;

    @Column(name = "is_cash")
    private Boolean isCash = false;

    @NotNull(message = "Organization ID is required")
    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    // Constructors
    public PaymentForm() {}

    public PaymentForm(String name, Boolean bankAccountIsRequested, Boolean isCash, Integer organizationId) {
        this.name = name;
        this.bankAccountIsRequested = bankAccountIsRequested;
        this.isCash = isCash;
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

    public Boolean getBankAccountIsRequested() {
        return bankAccountIsRequested;
    }

    public void setBankAccountIsRequested(Boolean bankAccountIsRequested) {
        this.bankAccountIsRequested = bankAccountIsRequested;
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

    @Override
    public String toString() {
        return "PaymentForm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bankAccountIsRequested=" + bankAccountIsRequested +
                ", isCash=" + isCash +
                ", organizationId=" + organizationId +
                '}';
    }
}