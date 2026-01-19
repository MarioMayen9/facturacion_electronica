package com.pos.backend.repository;

import com.pos.backend.entity.PaymentTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTermRepository extends JpaRepository<PaymentTerm, Integer> {
    
    List<PaymentTerm> findByOrganizationId(Integer organizationId);
    
    @Query("SELECT pt FROM PaymentTerm pt WHERE pt.paymentPeriod = 0 AND pt.organizationId = :organizationId")
    List<PaymentTerm> findCashTermsByOrganization(@Param("organizationId") Integer organizationId);
    
    @Query("SELECT pt FROM PaymentTerm pt WHERE pt.paymentPeriod > 0 AND pt.organizationId = :organizationId")
    List<PaymentTerm> findCreditTermsByOrganization(@Param("organizationId") Integer organizationId);
    
    List<PaymentTerm> findByNameContainingIgnoreCaseAndOrganizationId(String name, Integer organizationId);
    
    List<PaymentTerm> findByOrganizationIdOrderByName(Integer organizationId);
    
    Optional<PaymentTerm> findByIdAndOrganizationId(Integer id, Integer organizationId);
}