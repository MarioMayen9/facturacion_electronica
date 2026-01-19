package com.pos.backend.controller;

import com.pos.backend.entity.PaymentForm;
import com.pos.backend.service.PaymentFormService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment-forms")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentFormController {

    @Autowired
    private PaymentFormService paymentFormService;

    // Endpoint público para obtener todas las formas de pago
    @GetMapping
    public ResponseEntity<List<PaymentForm>> getAllPaymentForms() {
        try {
            List<PaymentForm> paymentForms = paymentFormService.findAll();
            return ResponseEntity.ok(paymentForms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<PaymentForm>> getAllByOrganization(@PathVariable Integer organizationId) {
        try {
            List<PaymentForm> paymentForms = paymentFormService.findAllByOrganization(organizationId);
            return ResponseEntity.ok(paymentForms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<PaymentForm> getById(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            Optional<PaymentForm> paymentForm = paymentFormService.findById(id, organizationId);
            return paymentForm.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PaymentForm paymentForm) {
        try {
            PaymentForm created = paymentFormService.create(paymentForm);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating payment form: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> update(@PathVariable Integer id, 
                                   @PathVariable Integer organizationId,
                                   @Valid @RequestBody PaymentForm paymentFormDetails) {
        try {
            PaymentForm updated = paymentFormService.update(id, paymentFormDetails, organizationId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error updating payment form: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating payment form: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            paymentFormService.delete(id, organizationId);
            return ResponseEntity.ok().body("Payment form deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deleting payment form: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting payment form: " + e.getMessage());
        }
    }
}