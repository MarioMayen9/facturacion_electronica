package com.pos.backend.service;

import com.pos.backend.entity.Client;
import com.pos.backend.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    public List<Client> findAllByOrganization(Integer organizationId) {
        return clientRepository.findAllByOrganizationId(organizationId);
    }
    
    public Optional<Client> findById(Integer id, Integer organizationId) {
        return clientRepository.findByIdAndOrganizationId(id, organizationId);
    }
    
    public Optional<Client> findByDui(String dui, Integer organizationId) {
        return clientRepository.findByDuiAndOrganizationId(dui, organizationId);
    }
    
    public Optional<Client> findByTaxId(String taxId, Integer organizationId) {
        return clientRepository.findByTaxIdAndOrganizationId(taxId, organizationId);
    }
    
    public Optional<Client> findByEmail(String email, Integer organizationId) {
        // Buscar por nombres, apellidos o name que contengan el email (adaptado al nuevo esquema)
        return clientRepository.findByNamesContainingIgnoreCaseAndOrganizationId(email, organizationId)
                .stream().findFirst();
    }
    
    public List<Client> findByTaxpayerClassification(Integer taxpayerClassificationId, Integer organizationId) {
        return clientRepository.findByTaxpayerClassificationIdAndOrganizationId(taxpayerClassificationId, organizationId);
    }
    
    public List<Client> searchByText(String searchText, Integer organizationId) {
        return clientRepository.findBySearchTextAndOrganizationId(searchText, organizationId);
    }

    public Client save(Client client, Integer organizationId) {
        // Validaciones básicas - usando el nuevo esquema SALE_Client
        if (client.getSingleIdentityDocumentNumber() != null && !client.getSingleIdentityDocumentNumber().trim().isEmpty()) {
            Optional<Client> existingClient = clientRepository.findByDuiAndOrganizationId(client.getSingleIdentityDocumentNumber(), organizationId);
            if (existingClient.isPresent() && !existingClient.get().getId().equals(client.getId())) {
                throw new RuntimeException("Ya existe un cliente con el DUI: " + client.getSingleIdentityDocumentNumber());
            }
        }
        
        if (client.getTaxId() != null && !client.getTaxId().trim().isEmpty()) {
            Optional<Client> existingClient = clientRepository.findByTaxIdAndOrganizationId(client.getTaxId(), organizationId);
            if (existingClient.isPresent() && !existingClient.get().getId().equals(client.getId())) {
                throw new RuntimeException("Ya existe un cliente con el NIT: " + client.getTaxId());
            }
        }

        client.setOrganizationId(organizationId);
        return clientRepository.save(client);
    }

    public Client update(Integer id, Client clientDetails, Integer organizationId) {
        Optional<Client> clientOpt = clientRepository.findByIdAndOrganizationId(id, organizationId);
        if (!clientOpt.isPresent()) {
            throw new RuntimeException("Cliente no encontrado");
        }
        
        Client client = clientOpt.get();
        
        // Verificar si el DUI ya existe para otra entidad
        if (clientDetails.getSingleIdentityDocumentNumber() != null && 
            !clientDetails.getSingleIdentityDocumentNumber().equals(client.getSingleIdentityDocumentNumber())) {
            Optional<Client> existingClient = clientRepository.findByDuiAndOrganizationId(
                clientDetails.getSingleIdentityDocumentNumber(), organizationId);
            if (existingClient.isPresent()) {
                throw new RuntimeException("Ya existe un cliente con el DUI: " + 
                    clientDetails.getSingleIdentityDocumentNumber());
            }
        }

        // Verificar si el NIT ya existe para otra entidad
        if (clientDetails.getTaxId() != null && !clientDetails.getTaxId().equals(client.getTaxId())) {
            Optional<Client> existingClient = clientRepository.findByTaxIdAndOrganizationId(
                clientDetails.getTaxId(), organizationId);
            if (existingClient.isPresent()) {
                throw new RuntimeException("Ya existe un cliente con el NIT: " + clientDetails.getTaxId());
            }
        }

        // Actualizar todos los campos del nuevo esquema SALE_Client
        if (clientDetails.getName() != null) {
            client.setName(clientDetails.getName());
        }
        if (clientDetails.getNames() != null) {
            client.setNames(clientDetails.getNames());
        }
        if (clientDetails.getSurnames() != null) {
            client.setSurnames(clientDetails.getSurnames());
        }
        if (clientDetails.getPhoneNumber() != null) {
            client.setPhoneNumber(clientDetails.getPhoneNumber());
        }
        if (clientDetails.getTradeName() != null) {
            client.setTradeName(clientDetails.getTradeName());
        }
        if (clientDetails.getGender() != null) {
            client.setGender(clientDetails.getGender());
        }
        if (clientDetails.getDateBirth() != null) {
            client.setDateBirth(clientDetails.getDateBirth());
        }
        if (clientDetails.getCommercialTrade() != null) {
            client.setCommercialTrade(clientDetails.getCommercialTrade());
        }
        if (clientDetails.getSingleIdentityDocumentNumber() != null) {
            client.setSingleIdentityDocumentNumber(clientDetails.getSingleIdentityDocumentNumber());
        }
        if (clientDetails.getTaxId() != null) {
            client.setTaxId(clientDetails.getTaxId());
        }
        if (clientDetails.getResidenceCard() != null) {
            client.setResidenceCard(clientDetails.getResidenceCard());
        }
        if (clientDetails.getPassport() != null) {
            client.setPassport(clientDetails.getPassport());
        }
        if (clientDetails.getOtherIdentityDocumentNumber() != null) {
            client.setOtherIdentityDocumentNumber(clientDetails.getOtherIdentityDocumentNumber());
        }
        if (clientDetails.getPersonType() != null) {
            client.setPersonType(clientDetails.getPersonType());
        }
        if (clientDetails.getStreet1() != null) {
            client.setStreet1(clientDetails.getStreet1());
        }
        if (clientDetails.getStateName() != null) {
            client.setStateName(clientDetails.getStateName());
        }
        if (clientDetails.getStateCode() != null) {
            client.setStateCode(clientDetails.getStateCode());
        }
        if (clientDetails.getCityName() != null) {
            client.setCityName(clientDetails.getCityName());
        }
        if (clientDetails.getCityCode() != null) {
            client.setCityCode(clientDetails.getCityCode());
        }
        if (clientDetails.getAreaName() != null) {
            client.setAreaName(clientDetails.getAreaName());
        }
        if (clientDetails.getAreaCode() != null) {
            client.setAreaCode(clientDetails.getAreaCode());
        }
        if (clientDetails.getIsVip() != null) {
            client.setIsVip(clientDetails.getIsVip());
        }
        if (clientDetails.getIsBToB() != null) {
            client.setIsBToB(clientDetails.getIsBToB());
        }
        if (clientDetails.getAdvertisingIsAccepted() != null) {
            client.setAdvertisingIsAccepted(clientDetails.getAdvertisingIsAccepted());
        }
        if (clientDetails.getPaymentTermId() != null) {
            client.setPaymentTermId(clientDetails.getPaymentTermId());
        }
        if (clientDetails.getDefaultPaymentFormId() != null) {
            client.setDefaultPaymentFormId(clientDetails.getDefaultPaymentFormId());
        }
        if (clientDetails.getReceivableAccount() != null) {
            client.setReceivableAccount(clientDetails.getReceivableAccount());
        }
        if (clientDetails.getTaxpayerClassificationId() != null) {
            client.setTaxpayerClassificationId(clientDetails.getTaxpayerClassificationId());
        }

        return clientRepository.save(client);
    }

    public void delete(Integer id, Integer organizationId) {
        Optional<Client> clientOpt = clientRepository.findByIdAndOrganizationId(id, organizationId);
        if (!clientOpt.isPresent()) {
            throw new RuntimeException("Cliente no encontrado");
        }
        
        Client clientEntity = clientOpt.get();
        // Soft delete - ya no tenemos campo active en el nuevo esquema
        // clientEntity.setActive(false);
        clientRepository.save(clientEntity);
    }

    public void hardDelete(Integer id, Integer organizationId) {
        Optional<Client> clientOpt = clientRepository.findByIdAndOrganizationId(id, organizationId);
        if (!clientOpt.isPresent()) {
            throw new RuntimeException("Cliente no encontrado");
        }
        
        Client clientEntity = clientOpt.get();
        clientRepository.delete(clientEntity);
    }
}