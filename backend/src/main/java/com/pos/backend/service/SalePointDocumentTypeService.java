package com.pos.backend.service;

import com.pos.backend.dto.SalePointDocumentTypeRequest;
import com.pos.backend.entity.DocumentType;
import com.pos.backend.entity.SalePoint;
import com.pos.backend.entity.SalePointDocumentType;
import com.pos.backend.repository.DocumentTypeRepository;
import com.pos.backend.repository.SalePointDocumentTypeRepository;
import com.pos.backend.repository.SalePointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalePointDocumentTypeService {
    
    private static final Logger logger = LoggerFactory.getLogger(SalePointDocumentTypeService.class);
    
    @Autowired
    private SalePointDocumentTypeRepository salePointDocumentTypeRepository;
    
    @Autowired
    private SalePointRepository salePointRepository;
    
    @Autowired
    private DocumentTypeRepository documentTypeRepository;
    
    // Get all sale point document types by organization
    public List<SalePointDocumentType> getAllByOrganization(Integer organizationId) {
        return salePointDocumentTypeRepository.findByOrganizationId(organizationId);
    }
    
    // Get by ID and organization
    public Optional<SalePointDocumentType> getByIdAndOrganization(Integer id, Integer organizationId) {
        return salePointDocumentTypeRepository.findByIdAndOrganizationId(id, organizationId);
    }
    
    // Get by sale point
    public List<SalePointDocumentType> getBySalePoint(Integer salePointId, Integer organizationId) {
        return salePointDocumentTypeRepository.findBySalePointIdAndOrganizationId(salePointId, organizationId);
    }
    
    // Get by document type
    public List<SalePointDocumentType> getByDocumentType(Integer documentTypeId, Integer organizationId) {
        return salePointDocumentTypeRepository.findByDocumentTypeIdAndOrganizationId(documentTypeId, organizationId);
    }
    
    // Get by sale point and document type
    public Optional<SalePointDocumentType> getBySalePointAndDocumentType(Integer salePointId, Integer documentTypeId, Integer organizationId) {
        return salePointDocumentTypeRepository.findBySalePointAndDocumentType(salePointId, documentTypeId, organizationId);
    }
    
    // Get by serial
    public List<SalePointDocumentType> getBySerial(String serial, Integer organizationId) {
        return salePointDocumentTypeRepository.findBySerial(serial, organizationId);
    }
    
    // Get available correlative for document
    public Optional<SalePointDocumentType> getAvailableCorrelativeForDocument(Integer salePointId, Integer documentTypeId, Integer organizationId) {
        return salePointDocumentTypeRepository.findAvailableCorrelativeForDocument(salePointId, documentTypeId, organizationId);
    }
    
    // Get next available number for a document type at a sale point
    public Integer getNextAvailableNumber(Integer salePointId, Integer documentTypeId, Integer organizationId) {
        Optional<SalePointDocumentType> spdt = getBySalePointAndDocumentType(salePointId, documentTypeId, organizationId);
        if (spdt.isPresent()) {
            SalePointDocumentType documentType = spdt.get();
            int nextNumber = documentType.getLatestNumberIssued() + 1;
            
            if (nextNumber <= documentType.getFinalNumberAuthorized()) {
                return nextNumber;
            } else {
                throw new RuntimeException("No more correlative numbers available for this document type. Authorized range exhausted.");
            }
        } else {
            throw new RuntimeException("No correlative configuration found for this sale point and document type");
        }
    }
    
    // Issue next number (updates the latest number issued)
    public Integer issueNextNumber(Integer salePointId, Integer documentTypeId, Integer organizationId) {
        Optional<SalePointDocumentType> spdt = getBySalePointAndDocumentType(salePointId, documentTypeId, organizationId);
        if (spdt.isPresent()) {
            SalePointDocumentType documentType = spdt.get();
            int nextNumber = documentType.getLatestNumberIssued() + 1;
            
            if (nextNumber <= documentType.getFinalNumberAuthorized()) {
                documentType.setLatestNumberIssued(nextNumber);
                salePointDocumentTypeRepository.save(documentType);
                return nextNumber;
            } else {
                throw new RuntimeException("No more correlative numbers available for this document type. Authorized range exhausted.");
            }
        } else {
            throw new RuntimeException("No correlative configuration found for this sale point and document type");
        }
    }
    
    // Check if there are available numbers
    public boolean hasAvailableNumbers(Integer salePointId, Integer documentTypeId, Integer organizationId) {
        Optional<SalePointDocumentType> spdt = getBySalePointAndDocumentType(salePointId, documentTypeId, organizationId);
        if (spdt.isPresent()) {
            SalePointDocumentType documentType = spdt.get();
            return documentType.getLatestNumberIssued() < documentType.getFinalNumberAuthorized();
        }
        return false;
    }
    
    // Get remaining numbers
    public Integer getRemainingNumbers(Integer salePointId, Integer documentTypeId, Integer organizationId) {
        Optional<SalePointDocumentType> spdt = getBySalePointAndDocumentType(salePointId, documentTypeId, organizationId);
        if (spdt.isPresent()) {
            SalePointDocumentType documentType = spdt.get();
            return documentType.getFinalNumberAuthorized() - documentType.getLatestNumberIssued();
        }
        return 0;
    }
    
    // Create new sale point document type
    public SalePointDocumentType create(SalePointDocumentTypeRequest request) {
        logger.info("🔧 === INICIANDO CREACIÓN EN SERVICIO ===");
        logger.info("🔧 Request recibido en servicio: {}", request);
        
        // Validate range
        logger.info("🔧 Validando rangos...");
        if (!request.isValidRange()) {
            logger.error("❌ Error: Rango inválido - número final debe ser mayor o igual al inicial");
            throw new RuntimeException("Invalid range: Final number must be greater than or equal to initial number");
        }
        logger.info("✅ Validación de rango OK");
        
        if (request.getLatestNumberIssued() != null && !request.isLatestNumberValid()) {
            logger.error("❌ Error: Último número emitido fuera del rango autorizado");
            throw new RuntimeException("Latest number issued must be within the authorized range");
        }
        logger.info("✅ Validación de último número emitido OK");
        
        // Verify sale point exists
        logger.info("🔧 Verificando que existe Sale Point ID: {} en Organization: {}", request.getSalePointId(), request.getOrganizationId());
        Optional<SalePoint> salePoint = salePointRepository.findByIdAndOrganizationId(request.getSalePointId(), request.getOrganizationId());
        if (!salePoint.isPresent()) {
            logger.error("❌ Error: Sale Point no encontrado. ID: {}, Organization: {}", request.getSalePointId(), request.getOrganizationId());
            throw new RuntimeException("Sale point not found");
        }
        logger.info("✅ Sale Point encontrado: {}", salePoint.get().getName());
        
        // Verify document type exists
        logger.info("🔧 Verificando que existe Document Type ID: {} en Organization: {}", request.getDocumentTypeId(), request.getOrganizationId());
        Optional<DocumentType> documentType = documentTypeRepository.findByIdAndOrganizationId(request.getDocumentTypeId(), request.getOrganizationId());
        if (!documentType.isPresent()) {
            logger.error("❌ Error: Document Type no encontrado. ID: {}, Organization: {}", request.getDocumentTypeId(), request.getOrganizationId());
            throw new RuntimeException("Document type not found");
        }
        logger.info("✅ Document Type encontrado: {} ({})", documentType.get().getName(), documentType.get().getCode());
        
        // Check if configuration already exists
        logger.info("🔧 Verificando si ya existe configuración para este Sale Point y Document Type...");
        Optional<SalePointDocumentType> existing = getBySalePointAndDocumentType(
            request.getSalePointId(), request.getDocumentTypeId(), request.getOrganizationId()
        );
        if (existing.isPresent()) {
            logger.error("❌ Error: Ya existe configuración de correlativo para este punto de venta y tipo de documento");
            throw new RuntimeException("Correlative configuration already exists for this sale point and document type");
        }
        logger.info("✅ No existe configuración previa, procediendo...");
        
        logger.info("🔧 Creando nueva entidad SalePointDocumentType...");
        SalePointDocumentType salePointDocumentType = new SalePointDocumentType();
        salePointDocumentType.setOrganizationId(request.getOrganizationId());
        salePointDocumentType.setSalePoint(salePoint.get());
        salePointDocumentType.setDocumentType(documentType.get());
        salePointDocumentType.setInitialNumberAuthorized(request.getInitialNumberAuthorized());
        salePointDocumentType.setFinalNumberAuthorized(request.getFinalNumberAuthorized());
        salePointDocumentType.setLatestNumberIssued(request.getLatestNumberIssued() != null ? request.getLatestNumberIssued() : request.getInitialNumberAuthorized() - 1);
        salePointDocumentType.setSerial(request.getSerial());
        salePointDocumentType.setPrintCustomFormatId(request.getPrintCustomFormatId());
        
        logger.info("🔧 Entidad creada, valores asignados:");
        logger.info("   - Organization ID: {}", salePointDocumentType.getOrganizationId());
        logger.info("   - Sale Point: {} (ID: {})", salePointDocumentType.getSalePoint().getName(), salePointDocumentType.getSalePoint().getId());
        logger.info("   - Document Type: {} (ID: {})", salePointDocumentType.getDocumentType().getName(), salePointDocumentType.getDocumentType().getId());
        logger.info("   - Initial Number: {}", salePointDocumentType.getInitialNumberAuthorized());
        logger.info("   - Final Number: {}", salePointDocumentType.getFinalNumberAuthorized());
        logger.info("   - Latest Issued: {}", salePointDocumentType.getLatestNumberIssued());
        logger.info("   - Serial: {}", salePointDocumentType.getSerial());
        
        logger.info("🔧 Guardando en base de datos...");
        SalePointDocumentType saved = salePointDocumentTypeRepository.save(salePointDocumentType);
        logger.info("✅ ¡Guardado exitoso! ID generado: {}", saved.getId());
        
        return saved;
    }
    
    // Update sale point document type
    public SalePointDocumentType update(Integer id, SalePointDocumentTypeRequest request) {
        Optional<SalePointDocumentType> existing = getByIdAndOrganization(id, request.getOrganizationId());
        if (!existing.isPresent()) {
            throw new RuntimeException("Sale point document type not found");
        }
        
        // Validate range
        if (!request.isValidRange()) {
            throw new RuntimeException("Invalid range: Final number must be greater than or equal to initial number");
        }
        
        SalePointDocumentType salePointDocumentType = existing.get();
        
        // Update fields (be careful with numbers already issued)
        if (request.getInitialNumberAuthorized() > salePointDocumentType.getLatestNumberIssued()) {
            salePointDocumentType.setInitialNumberAuthorized(request.getInitialNumberAuthorized());
        }
        
        if (request.getFinalNumberAuthorized() >= salePointDocumentType.getLatestNumberIssued()) {
            salePointDocumentType.setFinalNumberAuthorized(request.getFinalNumberAuthorized());
        }
        
        if (request.getLatestNumberIssued() != null && request.isLatestNumberValid()) {
            salePointDocumentType.setLatestNumberIssued(request.getLatestNumberIssued());
        }
        
        salePointDocumentType.setSerial(request.getSerial());
        salePointDocumentType.setPrintCustomFormatId(request.getPrintCustomFormatId());
        
        return salePointDocumentTypeRepository.save(salePointDocumentType);
    }
    
    // Delete sale point document type
    public void delete(Integer id, Integer organizationId) {
        Optional<SalePointDocumentType> existing = getByIdAndOrganization(id, organizationId);
        if (!existing.isPresent()) {
            throw new RuntimeException("Sale point document type not found");
        }
        
        // Check if any numbers have been issued
        SalePointDocumentType spdt = existing.get();
        if (spdt.getLatestNumberIssued() >= spdt.getInitialNumberAuthorized()) {
            throw new RuntimeException("Cannot delete correlative configuration with issued numbers. Consider updating the range instead.");
        }
        
        salePointDocumentTypeRepository.deleteById(id);
    }
}