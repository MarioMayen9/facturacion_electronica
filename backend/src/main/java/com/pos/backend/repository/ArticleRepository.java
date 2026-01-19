package com.pos.backend.repository;

import com.pos.backend.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    
    // Métodos básicos usando el esquema INV_Article
    List<Article> findByOrganizationId(Integer organizationId);
    
    Optional<Article> findByIdAndOrganizationId(Integer id, Integer organizationId);
    
    Optional<Article> findBySkuAndOrganizationId(String sku, Integer organizationId);
    
    @Query("SELECT a FROM Article a WHERE (LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.sku) LIKE LOWER(CONCAT('%', :search, '%'))) AND a.organizationId = :organizationId ORDER BY a.name")
    List<Article> findBySearchTextAndOrganizationId(@Param("search") String search, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT a FROM Article a WHERE a.retailPrice >= :minPrice AND a.retailPrice <= :maxPrice AND a.organizationId = :organizationId ORDER BY a.name")
    List<Article> findByRetailPriceBetweenAndOrganizationId(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, @Param("organizationId") Integer organizationId);
    
    List<Article> findByOrganizationIdOrderByName(Integer organizationId);
    
    @Query("SELECT a FROM Article a WHERE a.isVatExempt = :isVatExempt AND a.organizationId = :organizationId ORDER BY a.name")
    List<Article> findByIsVatExemptAndOrganizationId(@Param("isVatExempt") Boolean isVatExempt, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT COUNT(a) FROM Article a WHERE a.organizationId = :organizationId")
    Long countByOrganizationId(@Param("organizationId") Integer organizationId);
    
    @Query("SELECT a FROM Article a WHERE a.retailPrice > :minPrice AND a.organizationId = :organizationId ORDER BY a.retailPrice DESC")
    List<Article> findByRetailPriceGreaterThanAndOrganizationId(@Param("minPrice") BigDecimal minPrice, @Param("organizationId") Integer organizationId);
}