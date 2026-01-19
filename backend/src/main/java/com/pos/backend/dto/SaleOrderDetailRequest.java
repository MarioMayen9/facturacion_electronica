package com.pos.backend.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class SaleOrderDetailRequest {

    @NotNull(message = "El ID del artículo es obligatorio")
    @JsonProperty("article_id")
    private Integer articleId;

    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a 0")
    @JsonProperty("quantity")
    private BigDecimal quantity;

    @JsonProperty("retail_price")
    private BigDecimal retailPrice; // Se obtendrá del artículo si no se proporciona

    @JsonProperty("price")
    private BigDecimal price; // Se calculará desde retail_price si no se proporciona

    @JsonProperty("cost")
    private BigDecimal cost; // Se obtendrá del artículo si no se proporciona

    @Size(max = 255, message = "El nombre alternativo debe ser máximo 255 caracteres")
    @JsonProperty("alternative_name")
    private String alternativeName;

    @JsonProperty("order_related_document_id")
    private Integer orderRelatedDocumentId;

    // Constructor vacío
    public SaleOrderDetailRequest() {}

    // Getters y Setters
    public Integer getArticleId() { return articleId; }
    public void setArticleId(Integer articleId) { this.articleId = articleId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getRetailPrice() { return retailPrice; }
    public void setRetailPrice(BigDecimal retailPrice) { this.retailPrice = retailPrice; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public String getAlternativeName() { return alternativeName; }
    public void setAlternativeName(String alternativeName) { this.alternativeName = alternativeName; }

    public Integer getOrderRelatedDocumentId() { return orderRelatedDocumentId; }
    public void setOrderRelatedDocumentId(Integer orderRelatedDocumentId) { this.orderRelatedDocumentId = orderRelatedDocumentId; }

    // Método de validación
    public boolean isValid() {
        return articleId != null && quantity != null && quantity.compareTo(BigDecimal.ZERO) > 0;
    }
}