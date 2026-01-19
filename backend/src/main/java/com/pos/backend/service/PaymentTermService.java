package com.pos.backend.service;

import com.pos.backend.entity.PaymentTerm;
import com.pos.backend.repository.PaymentTermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentTermService {

    @Autowired
    private PaymentTermRepository paymentTermRepository;

    public List<PaymentTerm> findAll() {
        return paymentTermRepository.findAll();
    }

    public List<PaymentTerm> findAllByOrganization(Integer organizationId) {
        return paymentTermRepository.findByOrganizationId(organizationId);
    }

    public List<PaymentTerm> findCashTerms(Integer organizationId) {
        // Contado = payment_period = 0, usamos método básico
        return paymentTermRepository.findByOrganizationId(organizationId)
            .stream()
            .filter(pt -> pt.getPaymentPeriod() != null && pt.getPaymentPeriod() == 0)
            .toList();
    }

    public List<PaymentTerm> findCreditTerms(Integer organizationId) {
        // Crédito = payment_period > 0, usamos método básico
        return paymentTermRepository.findByOrganizationId(organizationId)
            .stream()
            .filter(pt -> pt.getPaymentPeriod() != null && pt.getPaymentPeriod() > 0)
            .toList();
    }

    public Optional<PaymentTerm> findById(Integer id, Integer organizationId) {
        return paymentTermRepository.findByIdAndOrganizationId(id, organizationId);
    }

    public List<PaymentTerm> searchByName(String name, Integer organizationId) {
        // Usar método básico y filtrar en memoria
        return paymentTermRepository.findByOrganizationId(organizationId)
            .stream()
            .filter(pt -> pt.getName() != null && pt.getName().toLowerCase().contains(name.toLowerCase()))
            .toList();
    }

    @Transactional
    public PaymentTerm create(PaymentTerm paymentTerm) {
        return paymentTermRepository.save(paymentTerm);
    }

    @Transactional
    public PaymentTerm update(Integer id, PaymentTerm paymentTermDetails, Integer organizationId) {
        Optional<PaymentTerm> optionalPaymentTerm = findById(id, organizationId);
        if (optionalPaymentTerm.isPresent()) {
            PaymentTerm paymentTerm = optionalPaymentTerm.get();
            paymentTerm.setName(paymentTermDetails.getName());
            paymentTerm.setPaymentPeriod(paymentTermDetails.getPaymentPeriod());
            paymentTerm.setIsSplitPaymentAllowed(paymentTermDetails.getIsSplitPaymentAllowed());
            return paymentTermRepository.save(paymentTerm);
        }
        throw new RuntimeException("PaymentTerm not found with id " + id);
    }

    @Transactional
    public void delete(Integer id, Integer organizationId) {
        Optional<PaymentTerm> paymentTerm = findById(id, organizationId);
        if (paymentTerm.isPresent()) {
            paymentTermRepository.delete(paymentTerm.get());
        } else {
            throw new RuntimeException("PaymentTerm not found with id " + id);
        }
    }
}