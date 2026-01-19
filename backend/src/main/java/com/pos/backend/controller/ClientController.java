package com.pos.backend.controller;

import com.pos.backend.dto.ClientRequest;
import com.pos.backend.entity.Client;
import com.pos.backend.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<Client>> getAllByOrganization(@PathVariable Integer organizationId) {
        try {
            List<Client> clients = clientService.findAllByOrganization(organizationId);
            return ResponseEntity.ok(clients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<Client> getById(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            Optional<Client> client = clientService.findById(id, organizationId);
            return client.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search/organization/{organizationId}")
    public ResponseEntity<List<Client>> search(@RequestParam String q, @PathVariable Integer organizationId) {
        try {
            List<Client> clients = clientService.searchByText(q, organizationId);
            return ResponseEntity.ok(clients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tax-id/{taxId}/organization/{organizationId}")
    public ResponseEntity<Client> getByTaxId(@PathVariable String taxId, @PathVariable Integer organizationId) {
        try {
            Optional<Client> client = clientService.findByTaxId(taxId, organizationId);
            return client.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/dui/{dui}/organization/{organizationId}")
    public ResponseEntity<Client> getByDui(@PathVariable String dui, @PathVariable Integer organizationId) {
        try {
            Optional<Client> client = clientService.findByDui(dui, organizationId);
            return client.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/email/{email}/organization/{organizationId}")
    public ResponseEntity<Client> getByEmail(@PathVariable String email, @PathVariable Integer organizationId) {
        try {
            Optional<Client> client = clientService.findByEmail(email, organizationId);
            return client.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/taxpayer-classification/{taxpayerClassificationId}/organization/{organizationId}")
    public ResponseEntity<List<Client>> getByTaxpayerClassification(@PathVariable Integer taxpayerClassificationId, 
                                                                   @PathVariable Integer organizationId) {
        try {
            List<Client> clients = clientService.findByTaxpayerClassification(taxpayerClassificationId, organizationId);
            return ResponseEntity.ok(clients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ClientRequest request) {
        try {
            Client client = new Client();
            
            // Mapear todos los campos del nuevo esquema SALE_Client
            client.setName(request.getName()); // Razón Social
            client.setNames(request.getNames()); // Nombres
            client.setSurnames(request.getSurnames()); // Apellidos
            client.setPhoneNumber(request.getPhoneNumber());
            client.setTradeName(request.getTradeName());
            client.setGender(request.getGender());
            client.setDateBirth(request.getDateBirth());
            client.setCommercialTrade(request.getCommercialTrade());
            client.setSingleIdentityDocumentNumber(request.getSingleIdentityDocumentNumber()); // DUI
            client.setTaxId(request.getTaxId()); // NIT
            client.setResidenceCard(request.getResidenceCard());
            client.setPassport(request.getPassport());
            client.setOtherIdentityDocumentNumber(request.getOtherIdentityDocumentNumber());
            client.setPersonType(request.getPersonType());
            client.setStreet1(request.getStreet1());
            client.setStateName(request.getStateName());
            client.setStateCode(request.getStateCode());
            client.setCityName(request.getCityName());
            client.setCityCode(request.getCityCode());
            client.setAreaName(request.getAreaName());
            client.setAreaCode(request.getAreaCode());
            client.setIsVip(request.getIsVip());
            client.setIsBToB(request.getIsBToB());
            client.setAdvertisingIsAccepted(request.getAdvertisingIsAccepted());
            client.setPaymentTermId(request.getPaymentTermId());
            client.setDefaultPaymentFormId(request.getDefaultPaymentFormId());
            client.setReceivableAccount(request.getReceivableAccount());
            client.setTaxpayerClassificationId(request.getTaxpayerClassificationId());
            client.setOrganizationId(request.getOrganizationId());

            Client created = clientService.save(client, request.getOrganizationId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error creating client: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating client: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> update(@PathVariable Integer id, 
                                   @PathVariable Integer organizationId,
                                   @Valid @RequestBody ClientRequest request) {
        try {
            Client clientDetails = new Client();

            // Mapear todos los campos del nuevo esquema SALE_Client para update
            clientDetails.setName(request.getName()); // Razón Social
            clientDetails.setNames(request.getNames()); // Nombres
            clientDetails.setSurnames(request.getSurnames()); // Apellidos
            clientDetails.setPhoneNumber(request.getPhoneNumber());
            clientDetails.setTradeName(request.getTradeName());
            clientDetails.setGender(request.getGender());
            clientDetails.setDateBirth(request.getDateBirth());
            clientDetails.setCommercialTrade(request.getCommercialTrade());
            clientDetails.setSingleIdentityDocumentNumber(request.getSingleIdentityDocumentNumber()); // DUI
            clientDetails.setTaxId(request.getTaxId()); // NIT
            clientDetails.setResidenceCard(request.getResidenceCard());
            clientDetails.setPassport(request.getPassport());
            clientDetails.setOtherIdentityDocumentNumber(request.getOtherIdentityDocumentNumber());
            clientDetails.setPersonType(request.getPersonType());
            clientDetails.setStreet1(request.getStreet1());
            clientDetails.setStateName(request.getStateName());
            clientDetails.setStateCode(request.getStateCode());
            clientDetails.setCityName(request.getCityName());
            clientDetails.setCityCode(request.getCityCode());
            clientDetails.setAreaName(request.getAreaName());
            clientDetails.setAreaCode(request.getAreaCode());
            clientDetails.setIsVip(request.getIsVip());
            clientDetails.setIsBToB(request.getIsBToB());
            clientDetails.setAdvertisingIsAccepted(request.getAdvertisingIsAccepted());
            clientDetails.setPaymentTermId(request.getPaymentTermId());
            clientDetails.setDefaultPaymentFormId(request.getDefaultPaymentFormId());
            clientDetails.setReceivableAccount(request.getReceivableAccount());
            clientDetails.setTaxpayerClassificationId(request.getTaxpayerClassificationId());

            Client updated = clientService.update(id, clientDetails, organizationId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error updating client: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating client: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/organization/{organizationId}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            clientService.delete(id, organizationId);
            return ResponseEntity.ok("Client deactivated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deactivating client: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deactivating client: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/organization/{organizationId}/hard")
    public ResponseEntity<?> hardDelete(@PathVariable Integer id, @PathVariable Integer organizationId) {
        try {
            clientService.hardDelete(id, organizationId);
            return ResponseEntity.ok("Client permanently deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deleting client: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting client: " + e.getMessage());
        }
    }
}