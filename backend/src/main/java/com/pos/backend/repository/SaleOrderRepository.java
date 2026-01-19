package com.pos.backend.repository;

import com.pos.backend.entity.SaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Repository
public interface SaleOrderRepository extends JpaRepository<SaleOrder, Integer> {
    
    @Query("SELECT so FROM SaleOrder so WHERE so.organizationId = :organizationId ORDER BY so.createdAt DESC")
    List<SaleOrder> findByOrganizationIdOrderByCreatedAtDesc(@Param("organizationId") Integer organizationId);
    
    @Query("SELECT so FROM SaleOrder so WHERE so.emissionDate >= :startDate AND so.emissionDate <= :endDate AND so.organizationId = :organizationId")
    List<SaleOrder> findByDateRangeAndOrganization(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("organizationId") Integer organizationId);
    
    List<SaleOrder> findByStatusAndOrganizationId(String status, Integer organizationId);
    
    List<SaleOrder> findByClientIdAndOrganizationId(Integer clientId, Integer organizationId);
    
    List<SaleOrder> findBySalePointIdAndOrganizationId(Integer salePointId, Integer organizationId);
    
    List<SaleOrder> findByCreatedByAndOrganizationId(Integer createdBy, Integer organizationId);
    
    @Query("SELECT so FROM SaleOrder so WHERE so.organizationId = :organizationId ORDER BY so.emissionDate DESC")
    List<SaleOrder> findByOrganizationIdOrderByEmissionDate(@Param("organizationId") Integer organizationId);
    
    Optional<SaleOrder> findByIdAndOrganizationId(Integer id, Integer organizationId);
    
    @Query("SELECT COALESCE(SUM(so.salesTotal), 0) FROM SaleOrder so WHERE so.emissionDate >= :startDate AND so.emissionDate <= :endDate AND so.organizationId = :organizationId AND so.status = 'E'")
    BigDecimal getTotalSalesByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("organizationId") Integer organizationId);
    
    // Consultas adicionales para DTE y fiscales
    @Query("SELECT so FROM SaleOrder so WHERE so.isDteProcessing = true AND so.organizationId = :organizationId")
    List<SaleOrder> findOrdersBeingProcessedForDte(@Param("organizationId") Integer organizationId);
    
    @Query("SELECT so FROM SaleOrder so WHERE so.controlNumber IS NOT NULL AND so.organizationId = :organizationId")
    List<SaleOrder> findOrdersWithDte(@Param("organizationId") Integer organizationId);
    
    @Query("SELECT so FROM SaleOrder so WHERE so.operationType = :operationType AND so.organizationId = :organizationId")
    List<SaleOrder> findByOperationType(@Param("operationType") String operationType, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT so FROM SaleOrder so WHERE so.documentNumber = :documentNumber AND so.salePointId = :salePointId AND so.organizationId = :organizationId")
    Optional<SaleOrder> findByDocumentNumberAndSalePoint(@Param("documentNumber") Integer documentNumber, @Param("salePointId") Integer salePointId, @Param("organizationId") Integer organizationId);
    
    // Métodos para cargar con detalles
    @Query("SELECT DISTINCT so FROM SaleOrder so LEFT JOIN FETCH so.details ORDER BY so.createdAt DESC")
    List<SaleOrder> findAllWithDetails();
    
    @Query("SELECT so FROM SaleOrder so LEFT JOIN FETCH so.details WHERE so.id = :id")
    Optional<SaleOrder> findByIdWithDetails(@Param("id") Integer id);
    
    @Query("SELECT DISTINCT so FROM SaleOrder so LEFT JOIN FETCH so.details WHERE so.clientId = :clientId AND so.organizationId = :organizationId ORDER BY so.createdAt DESC")
    List<SaleOrder> findByClientIdAndOrganizationIdWithDetails(@Param("clientId") Integer clientId, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT DISTINCT so FROM SaleOrder so LEFT JOIN FETCH so.details WHERE so.salePointId = :salePointId AND so.organizationId = :organizationId ORDER BY so.createdAt DESC")
    List<SaleOrder> findBySalePointIdAndOrganizationIdWithDetails(@Param("salePointId") Integer salePointId, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT DISTINCT so FROM SaleOrder so LEFT JOIN FETCH so.details WHERE so.createdBy = :createdBy AND so.organizationId = :organizationId ORDER BY so.createdAt DESC")
    List<SaleOrder> findByCreatedByAndOrganizationIdWithDetails(@Param("createdBy") Integer createdBy, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT DISTINCT so FROM SaleOrder so LEFT JOIN FETCH so.details WHERE so.status = :status AND so.organizationId = :organizationId ORDER BY so.createdAt DESC")
    List<SaleOrder> findByStatusAndOrganizationIdWithDetails(@Param("status") String status, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT DISTINCT so FROM SaleOrder so LEFT JOIN FETCH so.details WHERE so.organizationId = :organizationId ORDER BY so.createdAt DESC")
    List<SaleOrder> findByOrganizationIdOrderByCreatedAtDescWithDetails(@Param("organizationId") Integer organizationId);
    
    @Query("SELECT DISTINCT so FROM SaleOrder so LEFT JOIN FETCH so.details WHERE so.emissionDate >= :startDate AND so.emissionDate <= :endDate AND so.organizationId = :organizationId ORDER BY so.emissionDate DESC")
    List<SaleOrder> findByDateRangeAndOrganizationWithDetails(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("organizationId") Integer organizationId);
}