package com.pos.backend.repository;

import com.pos.backend.entity.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Integer> {
    
    List<DocumentType> findByOrganizationId(Integer organizationId);
    
    List<DocumentType> findByNameContainingIgnoreCaseAndOrganizationId(String name, Integer organizationId);
    
    @Query("SELECT dt FROM DocumentType dt WHERE dt.organizationId = :organizationId ORDER BY dt.name")
    List<DocumentType> findActiveDocumentTypes(@Param("organizationId") Integer organizationId);
    
    Optional<DocumentType> findByNameAndOrganizationId(String name, Integer organizationId);
    
    List<DocumentType> findByOrganizationIdOrderByName(Integer organizationId);
    
    Optional<DocumentType> findByIdAndOrganizationId(Integer id, Integer organizationId);
    
    Optional<DocumentType> findByCodeAndOrganizationId(String code, Integer organizationId);
    
    @Query("SELECT dt FROM DocumentType dt WHERE dt.organizationId = :organizationId AND dt.hasDecreaseEffect = true")
    List<DocumentType> findDecreaseDocumentTypes(@Param("organizationId") Integer organizationId);
    
    @Query("SELECT dt FROM DocumentType dt WHERE dt.organizationId = :organizationId AND dt.hasDecreaseEffect = false")
    List<DocumentType> findNormalDocumentTypes(@Param("organizationId") Integer organizationId);
    
    boolean existsByNameAndOrganizationId(String name, Integer organizationId);
    
    boolean existsByCodeAndOrganizationId(String code, Integer organizationId);
}