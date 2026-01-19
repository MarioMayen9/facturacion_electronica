package com.pos.backend.service;

import com.pos.backend.dto.DocumentTypeRequest;
import com.pos.backend.entity.DocumentType;
import com.pos.backend.repository.DocumentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DocumentTypeService {

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    public List<DocumentType> findAllByOrganization(Integer organizationId) {
        return documentTypeRepository.findByOrganizationId(organizationId);
    }

    public Optional<DocumentType> findById(Integer id, Integer organizationId) {
        return documentTypeRepository.findByIdAndOrganizationId(id, organizationId);
    }

    public List<DocumentType> searchByName(String name, Integer organizationId) {
        return documentTypeRepository.findByNameContainingIgnoreCaseAndOrganizationId(name, organizationId);
    }

    public Optional<DocumentType> findByCode(String code, Integer organizationId) {
        return documentTypeRepository.findByCodeAndOrganizationId(code, organizationId);
    }

    public List<DocumentType> findActiveDocumentTypes(Integer organizationId) {
        return documentTypeRepository.findActiveDocumentTypes(organizationId);
    }
    
    public List<DocumentType> findDecreaseDocumentTypes(Integer organizationId) {
        return documentTypeRepository.findDecreaseDocumentTypes(organizationId);
    }
    
    public List<DocumentType> findNormalDocumentTypes(Integer organizationId) {
        return documentTypeRepository.findNormalDocumentTypes(organizationId);
    }

    @Transactional
    public DocumentType create(DocumentTypeRequest request) {
        // Check if name already exists for this organization
        if (documentTypeRepository.existsByNameAndOrganizationId(request.getName(), request.getOrganizationId())) {
            throw new RuntimeException("Document type with this name already exists for this organization");
        }
        
        // Check if code already exists for this organization (if code is provided)
        if (request.getCode() != null && !request.getCode().isEmpty()) {
            if (documentTypeRepository.existsByCodeAndOrganizationId(request.getCode(), request.getOrganizationId())) {
                throw new RuntimeException("Document type with this code already exists for this organization");
            }
        }
        
        DocumentType documentType = new DocumentType();
        documentType.setName(request.getName());
        documentType.setCode(request.getCode());
        documentType.setHasDecreaseEffect(request.getHasDecreaseEffect() != null ? request.getHasDecreaseEffect() : false);
        documentType.setPriceWithVatIsVisible(request.getPriceWithVatIsVisible() != null ? request.getPriceWithVatIsVisible() : false);
        documentType.setOrganizationId(request.getOrganizationId());
        
        return documentTypeRepository.save(documentType);
    }

    @Transactional
    public DocumentType update(Integer id, DocumentTypeRequest request) {
        Optional<DocumentType> optionalDocumentType = findById(id, request.getOrganizationId());
        if (!optionalDocumentType.isPresent()) {
            throw new RuntimeException("Document type not found");
        }
        
        DocumentType documentType = optionalDocumentType.get();
        
        // Check if name already exists for another document type in this organization
        Optional<DocumentType> existingByName = documentTypeRepository.findByNameAndOrganizationId(request.getName(), request.getOrganizationId());
        if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
            throw new RuntimeException("Document type with this name already exists for this organization");
        }
        
        // Check if code already exists for another document type in this organization (if code is provided)
        if (request.getCode() != null && !request.getCode().isEmpty()) {
            Optional<DocumentType> existingByCode = documentTypeRepository.findByCodeAndOrganizationId(request.getCode(), request.getOrganizationId());
            if (existingByCode.isPresent() && !existingByCode.get().getId().equals(id)) {
                throw new RuntimeException("Document type with this code already exists for this organization");
            }
        }
        
        documentType.setName(request.getName());
        documentType.setCode(request.getCode());
        documentType.setHasDecreaseEffect(request.getHasDecreaseEffect() != null ? request.getHasDecreaseEffect() : false);
        documentType.setPriceWithVatIsVisible(request.getPriceWithVatIsVisible() != null ? request.getPriceWithVatIsVisible() : false);
        
        return documentTypeRepository.save(documentType);
    }

    @Transactional
    public void delete(Integer id, Integer organizationId) {
        Optional<DocumentType> optionalDocumentType = findById(id, organizationId);
        if (!optionalDocumentType.isPresent()) {
            throw new RuntimeException("Document type not found");
        }
        
        // Here you could add business logic to check if the document type is being used
        // in sale orders or other entities before deletion
        
        documentTypeRepository.deleteById(id);
    }
}