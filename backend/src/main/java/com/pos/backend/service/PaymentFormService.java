package com.pos.backend.service;

import com.pos.backend.entity.PaymentForm;
import com.pos.backend.repository.PaymentFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentFormService {

    @Autowired
    private PaymentFormRepository paymentFormRepository;

    public List<PaymentForm> findAll() {
        return paymentFormRepository.findAll();
    }

    public List<PaymentForm> findAllByOrganization(Integer organizationId) {
        return paymentFormRepository.findByOrganizationId(organizationId);
    }

    public Optional<PaymentForm> findById(Integer id, Integer organizationId) {
        return paymentFormRepository.findByIdAndOrganizationId(id, organizationId);
    }

    public List<PaymentForm> searchByName(String name, Integer organizationId) {
        // Usar método básico y filtrar en memoria
        return paymentFormRepository.findByOrganizationId(organizationId)
            .stream()
            .filter(pf -> pf.getName() != null && pf.getName().toLowerCase().contains(name.toLowerCase()))
            .toList();
    }

    public Optional<PaymentForm> findByName(String name, Integer organizationId) {
        // Usar método básico y filtrar en memoria
        return paymentFormRepository.findByOrganizationId(organizationId)
            .stream()
            .filter(pf -> pf.getName() != null && pf.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    @Transactional
    public PaymentForm create(PaymentForm paymentForm) {
        return paymentFormRepository.save(paymentForm);
    }

    @Transactional
    public PaymentForm update(Integer id, PaymentForm paymentFormDetails, Integer organizationId) {
        Optional<PaymentForm> optionalPaymentForm = findById(id, organizationId);
        if (optionalPaymentForm.isPresent()) {
            PaymentForm paymentForm = optionalPaymentForm.get();
            paymentForm.setName(paymentFormDetails.getName());
            paymentForm.setBankAccountIsRequested(paymentFormDetails.getBankAccountIsRequested());
            paymentForm.setIsCash(paymentFormDetails.getIsCash());
            return paymentFormRepository.save(paymentForm);
        }
        throw new RuntimeException("PaymentForm not found with id " + id);
    }

    @Transactional
    public void delete(Integer id, Integer organizationId) {
        Optional<PaymentForm> paymentForm = findById(id, organizationId);
        if (paymentForm.isPresent()) {
            paymentFormRepository.delete(paymentForm.get());
        } else {
            throw new RuntimeException("PaymentForm not found with id " + id);
        }
    }
}