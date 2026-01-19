package com.pos.backend.repository;

import com.pos.backend.entity.TaxpayerClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaxpayerClassificationRepository extends JpaRepository<TaxpayerClassification, Integer> {
    
    List<TaxpayerClassification> findByOrganizationIdOrderByName(Integer organizationId);
    
    Optional<TaxpayerClassification> findByIdAndOrganizationId(Integer id, Integer organizationId);
    
    boolean existsByNameAndOrganizationId(String name, Integer organizationId);
}