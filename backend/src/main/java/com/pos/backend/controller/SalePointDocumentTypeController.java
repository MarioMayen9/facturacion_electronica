package com.pos.backend.controller;

import com.pos.backend.dto.SalePointDocumentTypeRequest;
import com.pos.backend.entity.SalePointDocumentType;
import com.pos.backend.service.SalePointDocumentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sale-point-document-types")
@CrossOrigin(origins = "*")
public class SalePointDocumentTypeController {
    
    private static final Logger logger = LoggerFactory.getLogger(SalePointDocumentTypeController.class);
    
    @Autowired
    private SalePointDocumentTypeService salePointDocumentTypeService;
    
    // Get all sale point document types by organization
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<SalePointDocumentType>> getAllByOrganization(@PathVariable Integer organizationId) {
        try {
            List<SalePointDocumentType> salePointDocumentTypes = salePointDocumentTypeService.getAllByOrganization(organizationId);
            return ResponseEntity.ok(salePointDocumentTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get sale point document type by ID and organization
    @GetMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<SalePointDocumentType> getByIdAndOrganization(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            Optional<SalePointDocumentType> salePointDocumentType = salePointDocumentTypeService.getByIdAndOrganization(id, organizationId);
            return salePointDocumentType.map(ResponseEntity::ok)
                                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get by sale point
    @GetMapping("/sale-point/{salePointId}/organization/{organizationId}")
    public ResponseEntity<List<SalePointDocumentType>> getBySalePoint(@PathVariable Integer salePointId, @PathVariable Integer organizationId) {
        try {
            List<SalePointDocumentType> salePointDocumentTypes = salePointDocumentTypeService.getBySalePoint(salePointId, organizationId);
            return ResponseEntity.ok(salePointDocumentTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get by document type
    @GetMapping("/document-type/{documentTypeId}/organization/{organizationId}")
    public ResponseEntity<List<SalePointDocumentType>> getByDocumentType(@PathVariable Integer documentTypeId, @PathVariable Integer organizationId) {
        try {
            List<SalePointDocumentType> salePointDocumentTypes = salePointDocumentTypeService.getByDocumentType(documentTypeId, organizationId);
            return ResponseEntity.ok(salePointDocumentTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get by sale point and document type
    @GetMapping("/sale-point/{salePointId}/document-type/{documentTypeId}/organization/{organizationId}")
    public ResponseEntity<SalePointDocumentType> getBySalePointAndDocumentType(@PathVariable Integer salePointId, 
                                                                               @PathVariable Integer documentTypeId, 
                                                                               @PathVariable Integer organizationId) {
        try {
            Optional<SalePointDocumentType> salePointDocumentType = salePointDocumentTypeService.getBySalePointAndDocumentType(salePointId, documentTypeId, organizationId);
            return salePointDocumentType.map(ResponseEntity::ok)
                                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get by serial
    @GetMapping("/serial/{serial}/organization/{organizationId}")
    public ResponseEntity<List<SalePointDocumentType>> getBySerial(@PathVariable String serial, @PathVariable Integer organizationId) {
        try {
            List<SalePointDocumentType> salePointDocumentTypes = salePointDocumentTypeService.getBySerial(serial, organizationId);
            return ResponseEntity.ok(salePointDocumentTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get next available number
    @GetMapping("/sale-point/{salePointId}/document-type/{documentTypeId}/next-number/organization/{organizationId}")
    public ResponseEntity<Map<String, Object>> getNextAvailableNumber(@PathVariable Integer salePointId, 
                                                                       @PathVariable Integer documentTypeId, 
                                                                       @PathVariable Integer organizationId) {
        try {
            Integer nextNumber = salePointDocumentTypeService.getNextAvailableNumber(salePointId, documentTypeId, organizationId);
            return ResponseEntity.ok(Map.of("nextNumber", nextNumber));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                               .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(Map.of("error", "Internal server error"));
        }
    }
    
    // Issue next number
    @PostMapping("/sale-point/{salePointId}/document-type/{documentTypeId}/issue-number/organization/{organizationId}")
    public ResponseEntity<Map<String, Object>> issueNextNumber(@PathVariable Integer salePointId, 
                                                               @PathVariable Integer documentTypeId, 
                                                               @PathVariable Integer organizationId) {
        try {
            Integer issuedNumber = salePointDocumentTypeService.issueNextNumber(salePointId, documentTypeId, organizationId);
            return ResponseEntity.ok(Map.of("issuedNumber", issuedNumber, "message", "Number issued successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                               .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(Map.of("error", "Internal server error"));
        }
    }
    
    // Check availability
    @GetMapping("/sale-point/{salePointId}/document-type/{documentTypeId}/availability/organization/{organizationId}")
    public ResponseEntity<Map<String, Object>> checkAvailability(@PathVariable Integer salePointId, 
                                                                  @PathVariable Integer documentTypeId, 
                                                                  @PathVariable Integer organizationId) {
        try {
            boolean hasAvailable = salePointDocumentTypeService.hasAvailableNumbers(salePointId, documentTypeId, organizationId);
            Integer remaining = salePointDocumentTypeService.getRemainingNumbers(salePointId, documentTypeId, organizationId);
            
            return ResponseEntity.ok(Map.of(
                "hasAvailableNumbers", hasAvailable,
                "remainingNumbers", remaining
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(Map.of("error", "Internal server error"));
        }
    }
    
    // Create sale point document type
    @PostMapping
    public ResponseEntity<SalePointDocumentType> create(@Valid @RequestBody SalePointDocumentTypeRequest request) {
        logger.info("=== INICIANDO CREACIÓN DE SALE POINT DOCUMENT TYPE ===");
        logger.info("Request recibido: {}", request);
        logger.info("Sale Point ID: {}", request.getSalePointId());
        logger.info("Document Type ID: {}", request.getDocumentTypeId()); 
        logger.info("Organization ID: {}", request.getOrganizationId());
        logger.info("Serial: {}", request.getSerial());
        logger.info("Initial Number: {}", request.getInitialNumberAuthorized());
        logger.info("Final Number: {}", request.getFinalNumberAuthorized());
        logger.info("Latest Number Issued: {}", request.getLatestNumberIssued());
        logger.info("Print Custom Format ID: {}", request.getPrintCustomFormatId());
        
        try {
            logger.info("Llamando al servicio para crear...");
            SalePointDocumentType created = salePointDocumentTypeService.create(request);
            logger.info("✅ Sale Point Document Type creado exitosamente con ID: {}", created.getId());
            logger.info("Respuesta: {}", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            logger.error("❌ Error RuntimeException en la creación: {}", e.getMessage());
            logger.error("Stack trace: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                               .body(null);
        } catch (Exception e) {
            logger.error("❌ Error genérico en la creación: {}", e.getMessage());
            logger.error("Stack trace: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(null);
        }
    }
    
    // Update sale point document type
    @PutMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<SalePointDocumentType> update(@PathVariable Integer id, 
                                                         @PathVariable Integer organizationId,
                                                         @Valid @RequestBody SalePointDocumentTypeRequest request) {
        try {
            // Ensure organizationId matches
            request.setOrganizationId(organizationId);
            
            SalePointDocumentType updated = salePointDocumentTypeService.update(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Delete sale point document type
    @DeleteMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            salePointDocumentTypeService.delete(id, organizationId);
            return ResponseEntity.ok(Map.of("message", "Sale point document type deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                               .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                               .body(Map.of("error", "Internal server error"));
        }
    }
}