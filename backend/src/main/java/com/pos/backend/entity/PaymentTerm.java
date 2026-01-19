package com.pos.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PURCH_Payment_Term")
public class PaymentTerm {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "Payment period is required")
    @Min(value = 0, message = "Payment period cannot be negative")
    @Column(name = "payment_period", nullable = false)
    private Integer paymentPeriod;

    @Column(name = "is_split_payment_allowed")
    private Boolean isSplitPaymentAllowed = false;

    @NotNull(message = "Organization ID is required")
    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    // Constructors
    public PaymentTerm() {}

    public PaymentTerm(String name, Integer paymentPeriod, Boolean isSplitPaymentAllowed, Integer organizationId) {
        this.name = name;
        this.paymentPeriod = paymentPeriod;
        this.isSplitPaymentAllowed = isSplitPaymentAllowed;
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

    public Integer getPaymentPeriod() {
        return paymentPeriod;
    }

    public void setPaymentPeriod(Integer paymentPeriod) {
        this.paymentPeriod = paymentPeriod;
    }

    public Boolean getIsSplitPaymentAllowed() {
        return isSplitPaymentAllowed;
    }

    public void setIsSplitPaymentAllowed(Boolean isSplitPaymentAllowed) {
        this.isSplitPaymentAllowed = isSplitPaymentAllowed;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return "PaymentTerm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", paymentPeriod=" + paymentPeriod +
                ", isSplitPaymentAllowed=" + isSplitPaymentAllowed +
                ", organizationId=" + organizationId +
                '}';
    }
}