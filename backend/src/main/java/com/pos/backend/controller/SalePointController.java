package com.pos.backend.controller;

import com.pos.backend.dto.SalePointRequest;
import com.pos.backend.entity.SalePoint;
import com.pos.backend.service.SalePointService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sale-points")
public class SalePointController {

    @Autowired
    private SalePointService salePointService;

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<SalePoint>> getAllByOrganization(@PathVariable Integer organizationId) {
        try {
            List<SalePoint> salePoints = salePointService.findAllByOrganization(organizationId);
            return ResponseEntity.ok(salePoints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<SalePoint> getById(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            Optional<SalePoint> salePoint = salePointService.findById(id, organizationId);
            return salePoint.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/organization/{organizationId}")
    public ResponseEntity<List<SalePoint>> searchByName(@RequestParam String q, @PathVariable Integer organizationId) {
        try {
            List<SalePoint> salePoints = salePointService.searchByName(q, organizationId);
            return ResponseEntity.ok(salePoints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/name/{name}/organization/{organizationId}")
    public ResponseEntity<SalePoint> getByName(@PathVariable String name, @PathVariable Integer organizationId) {
        try {
            Optional<SalePoint> salePoint = salePointService.findByName(name, organizationId);
            return salePoint.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/organization/{organizationId}")
    public ResponseEntity<List<SalePoint>> getByUserAccess(@PathVariable Integer userId, @PathVariable Integer organizationId) {
        try {
            List<SalePoint> salePoints = salePointService.findByUserAccess(userId, organizationId);
            return ResponseEntity.ok(salePoints);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SalePointRequest request) {
        try {
            SalePoint salePoint = new SalePoint();
            salePoint.setName(request.getName());
            salePoint.setUserIds(request.getUserIds());
            salePoint.setOrganizationId(request.getOrganizationId());

            SalePoint createdSalePoint = salePointService.create(salePoint);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSalePoint);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating sale point: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal error creating sale point: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> update(@PathVariable Integer id, 
                                   @PathVariable Integer organizationId,
                                   @Valid @RequestBody SalePointRequest request) {
        try {
            SalePoint salePointDetails = new SalePoint();
            salePointDetails.setName(request.getName());
            salePointDetails.setUserIds(request.getUserIds());

            SalePoint updated = salePointService.update(id, salePointDetails, organizationId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error updating sale point: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating sale point: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            salePointService.delete(id, organizationId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deleting sale point: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting sale point: " + e.getMessage());
        }
    }

    // Endpoints para manejar acceso de usuarios
    @PutMapping("/{id}/users/add/{userId}/organization/{organizationId}")
    public ResponseEntity<?> addUserAccess(@PathVariable Integer id,
                                          @PathVariable Integer userId,
                                          @PathVariable Integer organizationId) {
        try {
            SalePoint updated = salePointService.addUserAccess(id, userId, organizationId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error adding user access: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding user access: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/users/remove/{userId}/organization/{organizationId}")
    public ResponseEntity<?> removeUserAccess(@PathVariable Integer id,
                                             @PathVariable Integer userId,
                                             @PathVariable Integer organizationId) {
        try {
            SalePoint updated = salePointService.removeUserAccess(id, userId, organizationId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error removing user access: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing user access: " + e.getMessage());
        }
    }
}