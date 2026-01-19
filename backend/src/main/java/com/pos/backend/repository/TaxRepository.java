package com.pos.backend.repository;

import com.pos.backend.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Integer> {
    
    List<Tax> findByOrganizationIdAndActiveTrue(Integer organizationId);
    
    List<Tax> findByNameContainingIgnoreCaseAndOrganizationIdAndActiveTrue(String name, Integer organizationId);
    
    Optional<Tax> findByCodeAndOrganizationId(String code, Integer organizationId);
    
    List<Tax> findByTaxTypeAndOrganizationIdAndActiveTrue(String taxType, Integer organizationId);
    
    @Query("SELECT t FROM Tax t WHERE t.rate >= :minRate AND t.rate <= :maxRate AND t.organizationId = :organizationId AND t.active = true")
    List<Tax> findByRateRangeAndOrganization(@Param("minRate") BigDecimal minRate, @Param("maxRate") BigDecimal maxRate, @Param("organizationId") Integer organizationId);
    
    List<Tax> findByOrganizationIdOrderByName(Integer organizationId);
    
    Optional<Tax> findByIdAndOrganizationId(Integer id, Integer organizationId);
}