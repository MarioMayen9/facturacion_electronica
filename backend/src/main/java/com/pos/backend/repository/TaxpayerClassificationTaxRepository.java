package com.pos.backend.repository;

import com.pos.backend.entity.TaxpayerClassificationTax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxpayerClassificationTaxRepository extends JpaRepository<TaxpayerClassificationTax, Integer> {
    
    List<TaxpayerClassificationTax> findByOrganizationId(Integer organizationId);
    
    List<TaxpayerClassificationTax> findByTaxpayerClassificationIdAndOrganizationId(Integer taxpayerClassificationId, Integer organizationId);
    
    List<TaxpayerClassificationTax> findByTaxIdAndOrganizationId(Integer taxId, Integer organizationId);
    
    void deleteByTaxpayerClassificationIdAndOrganizationId(Integer taxpayerClassificationId, Integer organizationId);
}