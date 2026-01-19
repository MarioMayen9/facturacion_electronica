package com.pos.backend.controller;

import com.pos.backend.dto.DocumentTypeRequest;
import com.pos.backend.entity.DocumentType;
import com.pos.backend.service.DocumentTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/document-types")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentTypeController {

    @Autowired
    private DocumentTypeService documentTypeService;

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<DocumentType>> getAllByOrganization(@PathVariable Integer organizationId) {
        try {
            List<DocumentType> documentTypes = documentTypeService.findAllByOrganization(organizationId);
            return ResponseEntity.ok(documentTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organization/{organizationId}/active")
    public ResponseEntity<List<DocumentType>> getActiveDocumentTypes(@PathVariable Integer organizationId) {
        try {
            List<DocumentType> documentTypes = documentTypeService.findActiveDocumentTypes(organizationId);
            return ResponseEntity.ok(documentTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<DocumentType> getById(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            Optional<DocumentType> documentType = documentTypeService.findById(id, organizationId);
            return documentType.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/organization/{organizationId}")
    public ResponseEntity<List<DocumentType>> searchByName(@RequestParam String name, @PathVariable Integer organizationId) {
        try {
            List<DocumentType> documentTypes = documentTypeService.searchByName(name, organizationId);
            return ResponseEntity.ok(documentTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code/{code}/organization/{organizationId}")
    public ResponseEntity<DocumentType> getByCode(@PathVariable String code, @PathVariable Integer organizationId) {
        try {
            Optional<DocumentType> documentType = documentTypeService.findByCode(code, organizationId);
            return documentType.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody DocumentTypeRequest documentTypeRequest) {
        try {
            DocumentType created = documentTypeService.create(documentTypeRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating document type: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> update(@PathVariable Integer id, 
                                   @PathVariable Integer organizationId,
                                   @Valid @RequestBody DocumentTypeRequest documentTypeRequest) {
        try {
            // Ensure organizationId matches
            documentTypeRequest.setOrganizationId(organizationId);
            
            DocumentType updated = documentTypeService.update(id, documentTypeRequest);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating document type: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            documentTypeService.delete(id, organizationId);
            return ResponseEntity.ok(Map.of("message", "Document type deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error deleting document type: " + e.getMessage()));
        }
    }
}