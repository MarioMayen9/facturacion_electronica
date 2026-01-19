package com.pos.backend.controller;

import com.pos.backend.dto.PaymentTermRequest;
import com.pos.backend.entity.PaymentTerm;
import com.pos.backend.service.PaymentTermService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment-terms")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentTermController {

    @Autowired
    private PaymentTermService paymentTermService;

    // Endpoint público para obtener todas las condiciones de pago
    @GetMapping
    public ResponseEntity<List<PaymentTerm>> getAllPaymentTerms() {
        try {
            List<PaymentTerm> paymentTerms = paymentTermService.findAll();
            return ResponseEntity.ok(paymentTerms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<PaymentTerm>> getAllByOrganization(@PathVariable Integer organizationId) {
        try {
            List<PaymentTerm> paymentTerms = paymentTermService.findAllByOrganization(organizationId);
            return ResponseEntity.ok(paymentTerms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organization/{organizationId}/cash")
    public ResponseEntity<List<PaymentTerm>> getCashTerms(@PathVariable Integer organizationId) {
        try {
            List<PaymentTerm> cashTerms = paymentTermService.findCashTerms(organizationId);
            return ResponseEntity.ok(cashTerms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organization/{organizationId}/credit")
    public ResponseEntity<List<PaymentTerm>> getCreditTerms(@PathVariable Integer organizationId) {
        try {
            List<PaymentTerm> creditTerms = paymentTermService.findCreditTerms(organizationId);
            return ResponseEntity.ok(creditTerms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<PaymentTerm> getById(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            Optional<PaymentTerm> paymentTerm = paymentTermService.findById(id, organizationId);
            return paymentTerm.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/organization/{organizationId}")
    public ResponseEntity<List<PaymentTerm>> searchByName(@RequestParam String name, @PathVariable Integer organizationId) {
        try {
            List<PaymentTerm> paymentTerms = paymentTermService.searchByName(name, organizationId);
            return ResponseEntity.ok(paymentTerms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PaymentTermRequest request) {
        try {
            PaymentTerm paymentTerm = new PaymentTerm();
            paymentTerm.setName(request.getName());
            paymentTerm.setPaymentPeriod(request.getDays() != null ? request.getDays() : 0);
            paymentTerm.setIsSplitPaymentAllowed(request.getIsCash() != null ? request.getIsCash() : false);
            paymentTerm.setOrganizationId(request.getOrganizationId());

            PaymentTerm created = paymentTermService.create(paymentTerm);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating payment term: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> update(@PathVariable Integer id, 
                                   @PathVariable Integer organizationId,
                                   @Valid @RequestBody PaymentTermRequest request) {
        try {
            PaymentTerm paymentTermDetails = new PaymentTerm();
            paymentTermDetails.setName(request.getName());
            paymentTermDetails.setPaymentPeriod(request.getDays() != null ? request.getDays() : 0);
            paymentTermDetails.setIsSplitPaymentAllowed(request.getIsCash() != null ? request.getIsCash() : false);

            PaymentTerm updated = paymentTermService.update(id, paymentTermDetails, organizationId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error updating payment term: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating payment term: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            paymentTermService.delete(id, organizationId);
            return ResponseEntity.ok("Payment term deactivated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deactivating payment term: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deactivating payment term: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/organization/{organizationId}/hard")
    public ResponseEntity<?> hardDelete(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            paymentTermService.delete(id, organizationId);
            return ResponseEntity.ok("Payment term permanently deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deleting payment term: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting payment term: " + e.getMessage());
        }
    }
}