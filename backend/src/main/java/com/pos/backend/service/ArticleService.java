package com.pos.backend.service;

import com.pos.backend.entity.Article;
import com.pos.backend.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    // Método público para obtener todos los artículos sin filtro de organización
    public List<Article> findAll() {
        return articleRepository.findAll();
    }
    
    public List<Article> findAllByOrganization(Integer organizationId) {
        return articleRepository.findByOrganizationId(organizationId);
    }
    
    public List<Article> findActiveByOrganization(Integer organizationId) {
        // En INV_Article no hay campo active, retornamos todos los artículos
        return articleRepository.findByOrganizationId(organizationId);
    }
    
    public Optional<Article> findById(Integer id, Integer organizationId) {
        return articleRepository.findByIdAndOrganizationId(id, organizationId);
    }
    
    public Optional<Article> findBySku(String sku, Integer organizationId) {
        return articleRepository.findBySkuAndOrganizationId(sku, organizationId);
    }
    
    public List<Article> searchByNameOrCode(String searchText, Integer organizationId) {
        return articleRepository.findBySearchTextAndOrganizationId(searchText, organizationId);
    }
    
    public List<Article> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Integer organizationId) {
        return articleRepository.findByRetailPriceBetweenAndOrganizationId(minPrice, maxPrice, organizationId);
    }

    public Article create(Article article) {
        // Validaciones básicas
        if (article.getSku() != null && !article.getSku().trim().isEmpty()) {
            Optional<Article> existingArticle = articleRepository.findBySkuAndOrganizationId(
                article.getSku(), article.getOrganizationId());
            if (existingArticle.isPresent()) {
                throw new RuntimeException("Ya existe un artículo con el SKU: " + article.getSku());
            }
        }

        // Validar precio
        if (article.getRetailPrice() == null || article.getRetailPrice().compareTo(BigDecimal.ZERO) < 0) {
            article.setRetailPrice(BigDecimal.ZERO);
        }

        return articleRepository.save(article);
    }

    public Article update(Integer id, Article articleDetails, Integer organizationId) {
        Optional<Article> articleOpt = articleRepository.findByIdAndOrganizationId(id, organizationId);
        if (!articleOpt.isPresent()) {
            throw new RuntimeException("Artículo no encontrado");
        }
        
        Article article = articleOpt.get();
        
        // Verificar si el SKU ya existe para otra entidad
        if (articleDetails.getSku() != null && !articleDetails.getSku().equals(article.getSku())) {
            Optional<Article> existingArticle = articleRepository.findBySkuAndOrganizationId(
                articleDetails.getSku(), organizationId);
            if (existingArticle.isPresent()) {
                throw new RuntimeException("Ya existe un artículo con el SKU: " + articleDetails.getSku());
            }
        }

        // Actualizar campos del esquema INV_Article
        if (articleDetails.getSku() != null) {
            article.setSku(articleDetails.getSku());
        }
        if (articleDetails.getName() != null) {
            article.setName(articleDetails.getName());
        }
        if (articleDetails.getDescription() != null) {
            article.setDescription(articleDetails.getDescription());
        }
        if (articleDetails.getImageUrl() != null) {
            article.setImageUrl(articleDetails.getImageUrl());
        }
        if (articleDetails.getRetailPrice() != null) {
            article.setRetailPrice(articleDetails.getRetailPrice());
        }
        if (articleDetails.getIsVatExempt() != null) {
            article.setIsVatExempt(articleDetails.getIsVatExempt());
        }

        return articleRepository.save(article);
    }

    public void delete(Integer id, Integer organizationId) {
        Optional<Article> articleOpt = articleRepository.findByIdAndOrganizationId(id, organizationId);
        if (!articleOpt.isPresent()) {
            throw new RuntimeException("Artículo no encontrado");
        }
        
        Article articleEntity = articleOpt.get();
        // Hard delete ya que no hay campo active en INV_Article
        articleRepository.delete(articleEntity);
    }

    public void hardDelete(Integer id, Integer organizationId) {
        Optional<Article> articleOpt = articleRepository.findByIdAndOrganizationId(id, organizationId);
        if (!articleOpt.isPresent()) {
            throw new RuntimeException("Artículo no encontrado");
        }
        
        Article articleEntity = articleOpt.get();
        articleRepository.delete(articleEntity);
    }

    // Métodos simplificados ya que INV_Article no maneja inventarios
    public Boolean checkStockAvailability(Integer id, BigDecimal requiredQuantity, Integer organizationId) {
        // En el nuevo esquema no hay manejo de inventarios
        // Retornar siempre true ya que es solo un catálogo
        Optional<Article> article = articleRepository.findByIdAndOrganizationId(id, organizationId);
        return article.isPresent();
    }

    // Método para compatibilidad con otros servicios que aún referencian getCode()
    public Optional<Article> findByCode(String code, Integer organizationId) {
        // Buscar por SKU en lugar de code
        return articleRepository.findBySkuAndOrganizationId(code, organizationId);
    }

    // Métodos placeholder para mantener compatibilidad con SaleOrderService
    // pero que no hacen nada ya que INV_Article no maneja inventarios
    public void updateStock(Integer id, BigDecimal newQuantity, Integer organizationId) {
        // No hacer nada - INV_Article no maneja inventarios
    }

    public void addStock(Integer id, BigDecimal quantityToAdd, Integer organizationId) {
        // No hacer nada - INV_Article no maneja inventarios  
    }

    public void subtractStock(Integer id, BigDecimal quantityToSubtract, Integer organizationId) {
        // No hacer nada - INV_Article no maneja inventarios
    }

    public List<Article> findLowStockArticles(BigDecimal minimumStock, Integer organizationId) {
        // Retornar lista vacía ya que no hay manejo de inventarios
        return List.of();
    }
}