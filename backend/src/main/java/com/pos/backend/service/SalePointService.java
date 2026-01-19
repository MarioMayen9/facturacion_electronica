package com.pos.backend.service;

import com.pos.backend.entity.SalePoint;
import com.pos.backend.repository.SalePointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalePointService {

    @Autowired
    private SalePointRepository salePointRepository;

    public List<SalePoint> findAllByOrganization(Integer organizationId) {
        return salePointRepository.findByOrganizationId(organizationId);
    }

    public Optional<SalePoint> findById(Integer id, Integer organizationId) {
        return salePointRepository.findByIdAndOrganizationId(id, organizationId);
    }

    public List<SalePoint> searchByName(String name, Integer organizationId) {
        return salePointRepository.findByNameContainingIgnoreCaseAndOrganizationId(name, organizationId);
    }

    public Optional<SalePoint> findByName(String name, Integer organizationId) {
        return salePointRepository.findByNameAndOrganizationId(name, organizationId);
    }

    public List<SalePoint> findByUserAccess(Integer userId, Integer organizationId) {
        String userIdStr = String.valueOf(userId);
        return salePointRepository.findByUserIdsContainingAndOrganizationId(userIdStr, organizationId);
    }

    @Transactional
    public SalePoint create(SalePoint salePoint) {
        // Validar que no existe otro punto de venta con el mismo nombre
        if (salePoint.getName() != null) {
            Optional<SalePoint> existingByName = salePointRepository.findByNameAndOrganizationId(salePoint.getName(), salePoint.getOrganizationId());
            if (existingByName.isPresent()) {
                throw new RuntimeException("Sale point with name '" + salePoint.getName() + "' already exists");
            }
        }

        return salePointRepository.save(salePoint);
    }

    @Transactional
    public SalePoint update(Integer id, SalePoint salePointDetails, Integer organizationId) {
        Optional<SalePoint> optionalSalePoint = findById(id, organizationId);
        if (!optionalSalePoint.isPresent()) {
            throw new RuntimeException("Sale point not found with id " + id);
        }
        
        SalePoint salePoint = optionalSalePoint.get();
        
        // Validar que no existe otro punto de venta con el mismo nombre (excluyendo el actual)
        if (salePointDetails.getName() != null && !salePointDetails.getName().equals(salePoint.getName())) {
            Optional<SalePoint> existingByName = salePointRepository.findByNameAndOrganizationId(salePointDetails.getName(), organizationId);
            if (existingByName.isPresent() && !existingByName.get().getId().equals(id)) {
                throw new RuntimeException("Sale point with name '" + salePointDetails.getName() + "' already exists");
            }
        }
        
        // Actualizar campos del esquema SALE_Sale_Point
        if (salePointDetails.getName() != null) {
            salePoint.setName(salePointDetails.getName());
        }
        if (salePointDetails.getUserIds() != null) {
            salePoint.setUserIds(salePointDetails.getUserIds());
        }
        
        return salePointRepository.save(salePoint);
    }

    @Transactional
    public void delete(Integer id, Integer organizationId) {
        Optional<SalePoint> salePoint = findById(id, organizationId);
        if (!salePoint.isPresent()) {
            throw new RuntimeException("Sale point not found with id " + id);
        }
        
        // Hard delete ya que SALE_Sale_Point no tiene campo active
        salePointRepository.delete(salePoint.get());
    }

    @Transactional
    public void hardDelete(Integer id, Integer organizationId) {
        Optional<SalePoint> salePoint = findById(id, organizationId);
        if (!salePoint.isPresent()) {
            throw new RuntimeException("Sale point not found with id " + id);
        }
        
        salePointRepository.delete(salePoint.get());
    }

    // Métodos utilitarios para manejo de userIds
    public SalePoint addUserAccess(Integer salePointId, Integer userId, Integer organizationId) {
        Optional<SalePoint> optionalSalePoint = findById(salePointId, organizationId);
        if (!optionalSalePoint.isPresent()) {
            throw new RuntimeException("Sale point not found with id " + salePointId);
        }
        
        SalePoint salePoint = optionalSalePoint.get();
        String currentUserIds = salePoint.getUserIds();
        String userIdStr = String.valueOf(userId);
        
        if (currentUserIds == null || currentUserIds.isEmpty()) {
            salePoint.setUserIds(userIdStr);
        } else if (!currentUserIds.contains(userIdStr)) {
            salePoint.setUserIds(currentUserIds + "," + userIdStr);
        }
        
        return salePointRepository.save(salePoint);
    }

    public SalePoint removeUserAccess(Integer salePointId, Integer userId, Integer organizationId) {
        Optional<SalePoint> optionalSalePoint = findById(salePointId, organizationId);
        if (!optionalSalePoint.isPresent()) {
            throw new RuntimeException("Sale point not found with id " + salePointId);
        }
        
        SalePoint salePoint = optionalSalePoint.get();
        String currentUserIds = salePoint.getUserIds();
        String userIdStr = String.valueOf(userId);
        
        if (currentUserIds != null && currentUserIds.contains(userIdStr)) {
            String updatedUserIds = currentUserIds.replaceAll("\\b" + userIdStr + "\\b", "")
                                                    .replaceAll(",,", ",")
                                                    .replaceAll("^,|,$", "");
            salePoint.setUserIds(updatedUserIds.isEmpty() ? null : updatedUserIds);
        }
        
        return salePointRepository.save(salePoint);
    }
}