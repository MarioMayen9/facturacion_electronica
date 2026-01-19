package com.pos.backend.repository;

import com.pos.backend.entity.SaleOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleOrderDetailRepository extends JpaRepository<SaleOrderDetail, Integer> {
    
    List<SaleOrderDetail> findByOrderIdAndOrganizationId(Integer orderId, Integer organizationId);
    
    Optional<SaleOrderDetail> findByOrderIdAndArticleIdAndOrganizationId(Integer orderId, Integer articleId, Integer organizationId);
    
    List<SaleOrderDetail> findByArticleIdAndOrganizationId(Integer articleId, Integer organizationId);
    
    void deleteByOrderIdAndOrganizationId(Integer orderId, Integer organizationId);
}