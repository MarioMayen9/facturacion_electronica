package com.pos.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class SalePointDocumentTypeRequest {
    
    @NotNull(message = "Sale point ID is required")
    @JsonProperty("sale_point_id")
    private Integer salePointId;
    
    @NotNull(message = "Document type ID is required")
    @JsonProperty("document_type_id")
    private Integer documentTypeId;
    
    @NotNull(message = "Initial number authorized is required")
    @Min(value = 1, message = "Initial number authorized must be greater than 0")
    @JsonProperty("initial_number_authorized")
    private Integer initialNumberAuthorized;
    
    @NotNull(message = "Final number authorized is required")
    @Min(value = 1, message = "Final number authorized must be greater than 0")
    @JsonProperty("final_number_authorized")
    private Integer finalNumberAuthorized;
    
    @Min(value = 0, message = "Latest number issued cannot be negative")
    @JsonProperty("latest_number_issued")
    private Integer latestNumberIssued = 0;
    
    @Size(max = 100, message = "Serial cannot exceed 100 characters")
    @JsonProperty("serial")
    private String serial;
    
    @JsonProperty("print_custom_format_id")
    private Integer printCustomFormatId;
    
    @NotNull(message = "Organization ID is required")
    @JsonProperty("organization_id")
    private Integer organizationId;
    
    // Constructor
    public SalePointDocumentTypeRequest() {}
    
    public SalePointDocumentTypeRequest(Integer salePointId, Integer documentTypeId, Integer initialNumberAuthorized, 
                                       Integer finalNumberAuthorized, Integer latestNumberIssued, String serial, 
                                       Integer printCustomFormatId, Integer organizationId) {
        this.salePointId = salePointId;
        this.documentTypeId = documentTypeId;
        this.initialNumberAuthorized = initialNumberAuthorized;
        this.finalNumberAuthorized = finalNumberAuthorized;
        this.latestNumberIssued = latestNumberIssued;
        this.serial = serial;
        this.printCustomFormatId = printCustomFormatId;
        this.organizationId = organizationId;
    }
    
    // Custom validation method
    public boolean isValidRange() {
        return initialNumberAuthorized != null && finalNumberAuthorized != null && 
               finalNumberAuthorized >= initialNumberAuthorized;
    }
    
    public boolean isLatestNumberValid() {
        if (latestNumberIssued == null || initialNumberAuthorized == null || finalNumberAuthorized == null) {
            return false;
        }
        
        // If no number has been issued yet, latestNumberIssued can be 0 or initialNumberAuthorized - 1
        if (latestNumberIssued == 0 || latestNumberIssued == initialNumberAuthorized - 1) {
            return true;
        }
        
        // If numbers have been issued, they must be within the authorized range
        return latestNumberIssued >= initialNumberAuthorized && latestNumberIssued <= finalNumberAuthorized;
    }
    
    // Getters and Setters
    public Integer getSalePointId() { return salePointId; }
    public void setSalePointId(Integer salePointId) { this.salePointId = salePointId; }
    
    public Integer getDocumentTypeId() { return documentTypeId; }
    public void setDocumentTypeId(Integer documentTypeId) { this.documentTypeId = documentTypeId; }
    
    public Integer getInitialNumberAuthorized() { return initialNumberAuthorized; }
    public void setInitialNumberAuthorized(Integer initialNumberAuthorized) { this.initialNumberAuthorized = initialNumberAuthorized; }
    
    public Integer getFinalNumberAuthorized() { return finalNumberAuthorized; }
    public void setFinalNumberAuthorized(Integer finalNumberAuthorized) { this.finalNumberAuthorized = finalNumberAuthorized; }
    
    public Integer getLatestNumberIssued() { return latestNumberIssued; }
    public void setLatestNumberIssued(Integer latestNumberIssued) { this.latestNumberIssued = latestNumberIssued; }
    
    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }
    
    public Integer getPrintCustomFormatId() { return printCustomFormatId; }
    public void setPrintCustomFormatId(Integer printCustomFormatId) { this.printCustomFormatId = printCustomFormatId; }
    
    public Integer getOrganizationId() { return organizationId; }
    public void setOrganizationId(Integer organizationId) { this.organizationId = organizationId; }
}