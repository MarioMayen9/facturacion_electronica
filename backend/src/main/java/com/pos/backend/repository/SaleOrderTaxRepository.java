package com.pos.backend.repository;

import com.pos.backend.entity.SaleOrderTax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SaleOrderTaxRepository extends JpaRepository<SaleOrderTax, Integer> {
    
    List<SaleOrderTax> findByOrderIdAndOrganizationId(Integer orderId, Integer organizationId);
    
    List<SaleOrderTax> findByTaxIdAndOrganizationId(Integer taxId, Integer organizationId);
    
    void deleteByOrderIdAndOrganizationId(Integer orderId, Integer organizationId);
}