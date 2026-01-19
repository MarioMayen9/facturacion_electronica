package com.pos.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class ClientRequest {

    @NotBlank(message = "Name (Razón Social) is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name; // Razón Social

    @Size(max = 255, message = "Names must not exceed 255 characters")
    private String names; // Nombres (para persona natural)

    @Size(max = 255, message = "Surnames must not exceed 255 characters")
    private String surnames; // Apellidos

    @Size(max = 60, message = "Phone number must not exceed 60 characters")
    private String phoneNumber; // Número de teléfono

    @Size(max = 255, message = "Trade name must not exceed 255 characters")
    private String tradeName; // Nombre comercial

    @Size(max = 1, message = "Gender must be a single character")
    private String gender; // Género (M/F)

    private LocalDate dateBirth; // Fecha de nacimiento

    @Size(max = 255, message = "Commercial trade must not exceed 255 characters")
    private String commercialTrade; // Giro

    @Size(max = 255, message = "DUI must not exceed 255 characters")
    private String singleIdentityDocumentNumber; // DUI

    @Size(max = 255, message = "Tax ID (NIT) must not exceed 255 characters")
    private String taxId; // NIT

    @Size(max = 255, message = "Residence card must not exceed 255 characters")
    private String residenceCard; // Carnet de residencia

    @Size(max = 255, message = "Passport must not exceed 255 characters")
    private String passport; // Pasaporte

    @Size(max = 255, message = "Other identity document must not exceed 255 characters")
    private String otherIdentityDocumentNumber; // Otro documento

    @NotBlank(message = "Person type is required (J: Jurídica, N: Natural)")
    @Size(max = 1, message = "Person type must be a single character")
    private String personType; // Tipo (J: Jurídica, N: Natural)

    private String street1; // Dirección

    @Size(max = 255, message = "State name must not exceed 255 characters")
    private String stateName; // Departamento

    @Size(max = 10, message = "State code must not exceed 10 characters")
    private String stateCode; // Código de Departamento

    @Size(max = 255, message = "City name must not exceed 255 characters")
    private String cityName; // Municipio

    @Size(max = 10, message = "City code must not exceed 10 characters")
    private String cityCode; // Código de Municipio

    @Size(max = 255, message = "Area name must not exceed 255 characters")
    private String areaName; // Distrito

    @Size(max = 10, message = "Area code must not exceed 10 characters")
    private String areaCode; // Código de Distrito

    private Boolean isVip = false; // Es VIP

    private Boolean isBToB = false; // Es B2B

    private Boolean advertisingIsAccepted = true; // Acepta recibir publicidad

    private Integer paymentTermId; // Condición de pago (FK)

    private Integer defaultPaymentFormId; // Forma de pago (FK)

    private Boolean receivableAccount = false; // Cuenta por cobrar

    private Integer taxpayerClassificationId;

    @NotNull(message = "Organization ID is required")
    private Integer organizationId;

    // Constructors
    public ClientRequest() {}

    // Getters and Setters
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
}