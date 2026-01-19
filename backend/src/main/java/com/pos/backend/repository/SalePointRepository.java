package com.pos.backend.repository;

import com.pos.backend.entity.SalePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalePointRepository extends JpaRepository<SalePoint, Integer> {
    
    // Métodos básicos usando el esquema SALE_Sale_Point
    List<SalePoint> findByOrganizationId(Integer organizationId);
    
    Optional<SalePoint> findByIdAndOrganizationId(Integer id, Integer organizationId);
    
    Optional<SalePoint> findByNameAndOrganizationId(String name, Integer organizationId);
    
    @Query("SELECT sp FROM SalePoint sp WHERE LOWER(sp.name) LIKE LOWER(CONCAT('%', :name, '%')) AND sp.organizationId = :organizationId ORDER BY sp.name")
    List<SalePoint> findByNameContainingIgnoreCaseAndOrganizationId(@Param("name") String name, @Param("organizationId") Integer organizationId);
    
    List<SalePoint> findByOrganizationIdOrderByName(Integer organizationId);
    
    @Query("SELECT sp FROM SalePoint sp WHERE sp.userIds LIKE CONCAT('%', :userId, '%') AND sp.organizationId = :organizationId ORDER BY sp.name")
    List<SalePoint> findByUserIdsContainingAndOrganizationId(@Param("userId") String userId, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT COUNT(sp) FROM SalePoint sp WHERE sp.organizationId = :organizationId")
    Long countByOrganizationId(@Param("organizationId") Integer organizationId);
    
    boolean existsByNameAndOrganizationId(String name, Integer organizationId);
}