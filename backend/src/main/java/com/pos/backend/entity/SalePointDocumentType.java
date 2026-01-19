package com.pos.backend.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "SALE_Sale_Point_Document_Type")
public class SalePointDocumentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "organization_id", nullable = false)
    @JsonProperty("organization_id")
    private Integer organizationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_point_id", nullable = false)
    @JsonProperty("sale_point_id")
    private SalePoint salePoint;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_type_id", nullable = false)
    @JsonProperty("document_type_id")
    private DocumentType documentType;
    
    @Column(name = "initial_number_authorized", nullable = false)
    @JsonProperty("initial_number_authorized")
    private Integer initialNumberAuthorized = 0;
    
    @Column(name = "final_number_authorized", nullable = false)
    @JsonProperty("final_number_authorized")
    private Integer finalNumberAuthorized = 0;
    
    @Column(name = "latest_number_issued", nullable = false)
    @JsonProperty("latest_number_issued")
    private Integer latestNumberIssued = 0;
    
    @Column(name = "serial", length = 100)
    @JsonProperty("serial")
    private String serial;
    
    @Column(name = "print_custom_format_id")
    @JsonProperty("print_custom_format_id")
    private Integer printCustomFormatId;
    
    public SalePointDocumentType() {}
    
    public SalePointDocumentType(Integer id, Integer organizationId, SalePoint salePoint, DocumentType documentType, 
                                Integer initialNumberAuthorized, Integer finalNumberAuthorized, Integer latestNumberIssued, 
                                String serial, Integer printCustomFormatId) {
        this.id = id;
        this.organizationId = organizationId;
        this.salePoint = salePoint;
        this.documentType = documentType;
        this.initialNumberAuthorized = initialNumberAuthorized;
        this.finalNumberAuthorized = finalNumberAuthorized;
        this.latestNumberIssued = latestNumberIssued;
        this.serial = serial;
        this.printCustomFormatId = printCustomFormatId;
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getOrganizationId() { return organizationId; }
    public void setOrganizationId(Integer organizationId) { this.organizationId = organizationId; }
    
    public SalePoint getSalePoint() { return salePoint; }
    public void setSalePoint(SalePoint salePoint) { this.salePoint = salePoint; }
    
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    
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
}