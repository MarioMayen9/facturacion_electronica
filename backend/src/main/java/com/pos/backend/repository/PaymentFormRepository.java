package com.pos.backend.repository;

import com.pos.backend.entity.PaymentForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentFormRepository extends JpaRepository<PaymentForm, Integer> {
    
    List<PaymentForm> findByOrganizationId(Integer organizationId);
    
    List<PaymentForm> findByNameContainingIgnoreCaseAndOrganizationId(String name, Integer organizationId);
    
    List<PaymentForm> findByOrganizationIdOrderByName(Integer organizationId);
    
    Optional<PaymentForm> findByIdAndOrganizationId(Integer id, Integer organizationId);
    
    List<PaymentForm> findByIsCashAndOrganizationId(Boolean isCash, Integer organizationId);
}