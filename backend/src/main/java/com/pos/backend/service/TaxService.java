package com.pos.backend.service;

import com.pos.backend.entity.Tax;
import com.pos.backend.repository.TaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaxService {

    @Autowired
    private TaxRepository taxRepository;

    public List<Tax> findAllByOrganization(Integer organizationId) {
        return taxRepository.findByOrganizationIdAndActiveTrue(organizationId);
    }

    public Optional<Tax> findById(Integer id, Integer organizationId) {
        return taxRepository.findByIdAndOrganizationId(id, organizationId);
    }

    public List<Tax> searchByName(String name, Integer organizationId) {
        return taxRepository.findByNameContainingIgnoreCaseAndOrganizationIdAndActiveTrue(name, organizationId);
    }

    public Optional<Tax> findByCode(String code, Integer organizationId) {
        return taxRepository.findByCodeAndOrganizationId(code, organizationId);
    }

    public List<Tax> findByTaxTypeAndOrganization(String taxType, Integer organizationId) {
        return taxRepository.findByTaxTypeAndOrganizationIdAndActiveTrue(taxType, organizationId);
    }

    public List<Tax> findByRateRange(BigDecimal minRate, BigDecimal maxRate, Integer organizationId) {
        return taxRepository.findByRateRangeAndOrganization(minRate, maxRate, organizationId);
    }

    @Transactional
    public Tax create(Tax tax) {
        tax.setCreatedAt(LocalDateTime.now());
        tax.setActive(true);
        return taxRepository.save(tax);
    }

    @Transactional
    public Tax update(Integer id, Tax taxDetails, Integer organizationId) {
        Optional<Tax> optionalTax = findById(id, organizationId);
        if (optionalTax.isPresent()) {
            Tax tax = optionalTax.get();
            tax.setCode(taxDetails.getCode());
            tax.setName(taxDetails.getName());
            tax.setDescription(taxDetails.getDescription());
            tax.setTaxType(taxDetails.getTaxType());
            tax.setRate(taxDetails.getRate());
            tax.setIsPercentage(taxDetails.getIsPercentage());
            tax.setActive(taxDetails.getActive());
            tax.setUpdatedAt(LocalDateTime.now());
            return taxRepository.save(tax);
        }
        throw new RuntimeException("Tax not found with id " + id);
    }

    @Transactional
    public void delete(Integer id, Integer organizationId) {
        Optional<Tax> tax = findById(id, organizationId);
        if (tax.isPresent()) {
            Tax taxEntity = tax.get();
            taxEntity.setActive(false);
            taxEntity.setUpdatedAt(LocalDateTime.now());
            taxRepository.save(taxEntity);
        } else {
            throw new RuntimeException("Tax not found with id " + id);
        }
    }

    @Transactional
    public void hardDelete(Integer id, Integer organizationId) {
        Optional<Tax> tax = findById(id, organizationId);
        if (tax.isPresent()) {
            taxRepository.delete(tax.get());
        } else {
            throw new RuntimeException("Tax not found with id " + id);
        }
    }

    public BigDecimal calculateTaxAmount(BigDecimal baseAmount, Integer taxId, Integer organizationId) {
        Optional<Tax> tax = findById(taxId, organizationId);
        if (tax.isPresent()) {
            Tax taxEntity = tax.get();
            if (taxEntity.getIsPercentage()) {
                return baseAmount.multiply(taxEntity.getRate().divide(BigDecimal.valueOf(100)));
            } else {
                return taxEntity.getRate();
            }
        }
        return BigDecimal.ZERO;
    }
}