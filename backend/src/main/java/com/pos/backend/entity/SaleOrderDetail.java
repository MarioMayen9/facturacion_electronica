package com.pos.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "SALE_Order_Detail")
public class SaleOrderDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "int unsigned")
    private Integer id;

    @NotNull(message = "Order ID is required")
    @Column(name = "order_id", columnDefinition = "int unsigned", nullable = false)
    private Integer orderId;

    @NotNull(message = "Article ID is required")
    @Column(name = "article_id", columnDefinition = "int unsigned", nullable = false)
    private Integer articleId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @Column(name = "quantity", precision = 8, scale = 2, nullable = false)
    private BigDecimal quantity = BigDecimal.valueOf(1.00);

    @NotNull(message = "Retail price is required")
    @DecimalMin(value = "0.00", message = "Retail price cannot be negative")
    @Column(name = "retail_price", precision = 13, scale = 2, nullable = false)
    private BigDecimal retailPrice = BigDecimal.ZERO; // Precio de Venta (con IVA) del catálogo

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.000000", message = "Price cannot be negative")
    @Column(name = "price", precision = 13, scale = 6, nullable = false)
    private BigDecimal price = BigDecimal.ZERO; // Precio unitario (sin IVA)

    @Column(name = "price_with_vat", precision = 13, scale = 2)
    private BigDecimal priceWithVat = BigDecimal.ZERO; // Precio unitario (con IVA)

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.000000", message = "Cost cannot be negative")
    @Column(name = "cost", precision = 13, scale = 6, nullable = false)
    private BigDecimal cost = BigDecimal.ZERO; // Costo del producto al momento de la venta

    @NotNull(message = "Subject amount is required")
    @Column(name = "subject_amount", precision = 13, scale = 2, nullable = false)
    private BigDecimal subjectAmount = BigDecimal.ZERO; // Monto afecto de la línea

    @Column(name = "subject_amount_with_vat", precision = 13, scale = 2)
    private BigDecimal subjectAmountWithVat = BigDecimal.ZERO; // Monto afecto con IVA de la línea

    @NotNull(message = "Exempt amount is required")
    @Column(name = "exempt_amount", precision = 13, scale = 2, nullable = false)
    private BigDecimal exemptAmount = BigDecimal.ZERO; // Monto exento de la línea

    @NotNull(message = "Not subject amount is required")
    @Column(name = "not_subject_amount", precision = 13, scale = 2, nullable = false)
    private BigDecimal notSubjectAmount = BigDecimal.ZERO; // Monto no sujeto de la línea

    @Column(name = "alternative_name", length = 255)
    private String alternativeName; // Nombre alternativo del producto en la venta

    @Column(name = "order_related_document_id", columnDefinition = "int unsigned")
    private Integer orderRelatedDocumentId; // FK a SALE_Order para relacionar DTE (devoluciones)

    @NotNull(message = "Organization ID is required")
    @Column(name = "organization_id", columnDefinition = "int unsigned", nullable = false)
    private Integer organizationId;

    // Constructors
    public SaleOrderDetail() {}

    public SaleOrderDetail(Integer orderId, Integer articleId, BigDecimal quantity, 
                          BigDecimal retailPrice, BigDecimal price, BigDecimal cost, Integer organizationId) {
        this.orderId = orderId;
        this.articleId = articleId;
        this.quantity = quantity;
        this.retailPrice = retailPrice;
        this.price = price;
        this.cost = cost;
        this.organizationId = organizationId;
        
        // Calcular campos automáticamente
        calculateAmounts();
    }

    // Método helper para calcular montos fiscales
    public void calculateAmounts() {
        // Calcular price_with_vat si no está establecido
        if (priceWithVat == null || priceWithVat.compareTo(BigDecimal.ZERO) == 0) {
            priceWithVat = retailPrice; // El precio retail ya tiene IVA
        }
        
        // Para esta implementación, asumimos que todos los montos son afectos (gravados)
        // En una implementación real, esto dependería del tipo de producto/artículo
        BigDecimal lineTotal = quantity.multiply(price);
        BigDecimal lineTotalWithVat = quantity.multiply(priceWithVat);
        
        // Monto afecto (sin IVA)
        subjectAmount = lineTotal;
        
        // Monto afecto con IVA
        subjectAmountWithVat = lineTotalWithVat;
        
        // Por defecto, exempt y not_subject son cero (productos gravados)
        exemptAmount = BigDecimal.ZERO;
        notSubjectAmount = BigDecimal.ZERO;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getArticleId() { return articleId; }
    public void setArticleId(Integer articleId) { this.articleId = articleId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { 
        this.quantity = quantity;
        calculateAmounts(); // Recalcular cuando cambie la cantidad
    }

    public BigDecimal getRetailPrice() { return retailPrice; }
    public void setRetailPrice(BigDecimal retailPrice) { 
        this.retailPrice = retailPrice;
        calculateAmounts(); // Recalcular cuando cambie el precio
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { 
        this.price = price;
        calculateAmounts(); // Recalcular cuando cambie el precio
    }

    public BigDecimal getPriceWithVat() { return priceWithVat; }
    public void setPriceWithVat(BigDecimal priceWithVat) { this.priceWithVat = priceWithVat; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public BigDecimal getSubjectAmount() { return subjectAmount; }
    public void setSubjectAmount(BigDecimal subjectAmount) { this.subjectAmount = subjectAmount; }

    public BigDecimal getSubjectAmountWithVat() { return subjectAmountWithVat; }
    public void setSubjectAmountWithVat(BigDecimal subjectAmountWithVat) { this.subjectAmountWithVat = subjectAmountWithVat; }

    public BigDecimal getExemptAmount() { return exemptAmount; }
    public void setExemptAmount(BigDecimal exemptAmount) { this.exemptAmount = exemptAmount; }

    public BigDecimal getNotSubjectAmount() { return notSubjectAmount; }
    public void setNotSubjectAmount(BigDecimal notSubjectAmount) { this.notSubjectAmount = notSubjectAmount; }

    public String getAlternativeName() { return alternativeName; }
    public void setAlternativeName(String alternativeName) { this.alternativeName = alternativeName; }

    public Integer getOrderRelatedDocumentId() { return orderRelatedDocumentId; }
    public void setOrderRelatedDocumentId(Integer orderRelatedDocumentId) { this.orderRelatedDocumentId = orderRelatedDocumentId; }

    public Integer getOrganizationId() { return organizationId; }
    public void setOrganizationId(Integer organizationId) { this.organizationId = organizationId; }

    @Override
    public String toString() {
        return "SaleOrderDetail{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", articleId=" + articleId +
                ", quantity=" + quantity +
                ", retailPrice=" + retailPrice +
                ", subjectAmount=" + subjectAmount +
                '}';
    }
}