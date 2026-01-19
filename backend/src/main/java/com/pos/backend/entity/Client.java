package com.pos.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SALE_Client")
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name (Razón Social) is required")
    @Size(max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name; // Razón Social

    @Size(max = 255)
    @Column(name = "names", length = 255)
    private String names; // Nombres (para persona natural)

    @Size(max = 255)
    @Column(name = "surnames", length = 255)
    private String surnames; // Apellidos

    @Size(max = 60)
    @Column(name = "phone_number", length = 60)
    private String phoneNumber; // Número de teléfono

    @Size(max = 255)
    @Column(name = "trade_name", length = 255)
    private String tradeName; // Nombre comercial

    @Size(max = 1)
    @Column(name = "gender", length = 1)
    private String gender; // Género (M/F)

    @Column(name = "date_birth")
    private LocalDate dateBirth; // Fecha de nacimiento

    @Size(max = 255)
    @Column(name = "commercial_trade", length = 255)
    private String commercialTrade; // Giro

    @Size(max = 255)
    @Column(name = "single_identity_document_number", length = 255)
    private String singleIdentityDocumentNumber; // DUI

    @Size(max = 255)
    @Column(name = "tax_id", length = 255)
    private String taxId; // NIT

    @Size(max = 255)
    @Column(name = "residence_card", length = 255)
    private String residenceCard; // Carnet de residencia

    @Size(max = 255)
    @Column(name = "passport", length = 255)
    private String passport; // Pasaporte

    @Size(max = 255)
    @Column(name = "other_identity_document_number", length = 255)
    private String otherIdentityDocumentNumber; // Otro documento

    @Size(max = 1)
    @Column(name = "person_type", length = 1)
    private String personType; // Tipo (J: Jurídica, N: Natural)

    @Column(name = "street1")
    private String street1; // Dirección

    @Size(max = 255)
    @Column(name = "state_name", length = 255)
    private String stateName; // Departamento

    @Size(max = 10)
    @Column(name = "state_code", length = 10)
    private String stateCode; // Código de Departamento

    @Size(max = 255)
    @Column(name = "city_name", length = 255)
    private String cityName; // Municipio

    @Size(max = 10)
    @Column(name = "city_code", length = 10)
    private String cityCode; // Código de Municipio

    @Size(max = 255)
    @Column(name = "area_name", length = 255)
    private String areaName; // Distrito

    @Size(max = 10)
    @Column(name = "area_code", length = 10)
    private String areaCode; // Código de Distrito

    @Column(name = "is_vip", nullable = false)
    private Boolean isVip = false; // Es VIP

    @Column(name = "is_b_to_b", nullable = false)
    private Boolean isBToB = false; // Es B2B

    @Column(name = "advertising_is_accepted", nullable = false)
    private Boolean advertisingIsAccepted = true; // Acepta recibir publicidad

    @Column(name = "payment_term_id")
    private Integer paymentTermId; // Condición de pago (FK)

    @Column(name = "default_payment_form_id")
    private Integer defaultPaymentFormId; // Forma de pago (FK)

    @Column(name = "receivable_account", nullable = false)
    private Boolean receivableAccount = false; // Cuenta por cobrar

    @Column(name = "taxpayer_classification_id")
    private Integer taxpayerClassificationId;

    @NotNull(message = "Organization ID is required")
    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    // Constructors
    public Client() {}

    public Client(String name, String personType, Integer organizationId) {
        this.name = name;
        this.personType = personType;
        this.organizationId = organizationId;
        this.isVip = false;
        this.isBToB = false;
        this.advertisingIsAccepted = true;
        this.receivableAccount = false;
    }

    @PrePersist
    protected void onCreate() {
        if (isVip == null) {
            isVip = false;
        }
        if (isBToB == null) {
            isBToB = false;
        }
        if (advertisingIsAccepted == null) {
            advertisingIsAccepted = true;
        }
        if (receivableAccount == null) {
            receivableAccount = false;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(LocalDate dateBirth) {
        this.dateBirth = dateBirth;
    }

    public String getCommercialTrade() {
        return commercialTrade;
    }

    public void setCommercialTrade(String commercialTrade) {
        this.commercialTrade = commercialTrade;
    }

    public String getSingleIdentityDocumentNumber() {
        return singleIdentityDocumentNumber;
    }

    public void setSingleIdentityDocumentNumber(String singleIdentityDocumentNumber) {
        this.singleIdentityDocumentNumber = singleIdentityDocumentNumber;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getResidenceCard() {
        return residenceCard;
    }

    public void setResidenceCard(String residenceCard) {
        this.residenceCard = residenceCard;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getOtherIdentityDocumentNumber() {
        return otherIdentityDocumentNumber;
    }

    public void setOtherIdentityDocumentNumber(String otherIdentityDocumentNumber) {
        this.otherIdentityDocumentNumber = otherIdentityDocumentNumber;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Boolean getIsVip() {
        return isVip;
    }

    public void setIsVip(Boolean isVip) {
        this.isVip = isVip;
    }

    public Boolean getIsBToB() {
        return isBToB;
    }

    public void setIsBToB(Boolean isBToB) {
        this.isBToB = isBToB;
    }

    public Boolean getAdvertisingIsAccepted() {
        return advertisingIsAccepted;
    }

    public void setAdvertisingIsAccepted(Boolean advertisingIsAccepted) {
        this.advertisingIsAccepted = advertisingIsAccepted;
    }

    public Integer getPaymentTermId() {
        return paymentTermId;
    }

    public void setPaymentTermId(Integer paymentTermId) {
        this.paymentTermId = paymentTermId;
    }

    public Integer getDefaultPaymentFormId() {
        return defaultPaymentFormId;
    }

    public void setDefaultPaymentFormId(Integer defaultPaymentFormId) {
        this.defaultPaymentFormId = defaultPaymentFormId;
    }

    public Boolean getReceivableAccount() {
        return receivableAccount;
    }

    public void setReceivableAccount(Boolean receivableAccount) {
        this.receivableAccount = receivableAccount;
    }

    public Integer getTaxpayerClassificationId() {
        return taxpayerClassificationId;
    }

    public void setTaxpayerClassificationId(Integer taxpayerClassificationId) {
        this.taxpayerClassificationId = taxpayerClassificationId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", names='" + names + '\'' +
                ", surnames='" + surnames + '\'' +
                ", personType='" + personType + '\'' +
                ", taxId='" + taxId + '\'' +
                ", singleIdentityDocumentNumber='" + singleIdentityDocumentNumber + '\'' +
                '}';
    }
}