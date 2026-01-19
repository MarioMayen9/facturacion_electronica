package com.pos.backend.repository;

import com.pos.backend.entity.SalePointDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalePointDocumentTypeRepository extends JpaRepository<SalePointDocumentType, Integer> {
    
    List<SalePointDocumentType> findByOrganizationId(Integer organizationId);
    
    Optional<SalePointDocumentType> findByIdAndOrganizationId(Integer id, Integer organizationId);
    
    List<SalePointDocumentType> findBySalePointIdAndOrganizationId(Integer salePointId, Integer organizationId);
    
    List<SalePointDocumentType> findByDocumentTypeIdAndOrganizationId(Integer documentTypeId, Integer organizationId);
    
    @Query("SELECT spdt FROM SalePointDocumentType spdt WHERE spdt.salePoint.id = :salePointId AND spdt.documentType.id = :documentTypeId AND spdt.organizationId = :organizationId")
    Optional<SalePointDocumentType> findBySalePointAndDocumentType(@Param("salePointId") Integer salePointId, @Param("documentTypeId") Integer documentTypeId, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT spdt FROM SalePointDocumentType spdt WHERE spdt.organizationId = :organizationId")
    List<SalePointDocumentType> findAvailableDocumentTypes(@Param("organizationId") Integer organizationId);
    
    @Query("SELECT spdt FROM SalePointDocumentType spdt WHERE spdt.salePoint.id = :salePointId AND spdt.organizationId = :organizationId")
    List<SalePointDocumentType> findAvailableDocumentTypesBySalePoint(@Param("salePointId") Integer salePointId, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT spdt FROM SalePointDocumentType spdt WHERE spdt.salePoint.id = :salePointId AND spdt.documentType.id = :documentTypeId AND spdt.organizationId = :organizationId AND spdt.latestNumberIssued < spdt.finalNumberAuthorized")
    Optional<SalePointDocumentType> findAvailableCorrelativeForDocument(@Param("salePointId") Integer salePointId, @Param("documentTypeId") Integer documentTypeId, @Param("organizationId") Integer organizationId);
    
    @Query("SELECT spdt FROM SalePointDocumentType spdt WHERE spdt.serial = :serial AND spdt.organizationId = :organizationId")
    List<SalePointDocumentType> findBySerial(@Param("serial") String serial, @Param("organizationId") Integer organizationId);
}