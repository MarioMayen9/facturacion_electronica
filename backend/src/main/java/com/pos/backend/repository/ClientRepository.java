package com.pos.backend.repository;

import com.pos.backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    
    // Buscar todos los clientes de una organización (sin filtro active ya que no existe en SALE_Client)
    List<Client> findByOrganizationId(Integer organizationId);
    
    // Buscar todos los clientes de una organización ordenados por name
    List<Client> findAllByOrganizationId(Integer organizationId);
    
    // Buscar por DUI (single_identity_document_number)
    @Query("SELECT c FROM Client c WHERE c.singleIdentityDocumentNumber = :dui AND c.organizationId = :organizationId")
    Optional<Client> findByDuiAndOrganizationId(@Param("dui") String dui, @Param("organizationId") Integer organizationId);
    
    // Buscar por NIT (tax_id)
    Optional<Client> findByTaxIdAndOrganizationId(String taxId, Integer organizationId);
    
    // Buscar por clasificación de contribuyente
    List<Client> findByTaxpayerClassificationIdAndOrganizationId(Integer taxpayerClassificationId, Integer organizationId);
    
    // Buscar por ID y organización
    Optional<Client> findByIdAndOrganizationId(Integer id, Integer organizationId);
    
    // Buscar por nombres que contengan texto
    List<Client> findByNamesContainingIgnoreCaseAndOrganizationId(String names, Integer organizationId);
    
    // Búsqueda general por texto (name, names, surnames, trade_name)
    @Query("SELECT c FROM Client c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.names) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.surnames) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(c.tradeName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "c.singleIdentityDocumentNumber LIKE CONCAT('%', :searchText, '%') OR " +
           "c.taxId LIKE CONCAT('%', :searchText, '%')) AND " +
           "c.organizationId = :organizationId")
    List<Client> findBySearchTextAndOrganizationId(@Param("searchText") String searchText, 
                                                   @Param("organizationId") Integer organizationId);
    
    // Buscar clientes VIP
    List<Client> findByIsVipTrueAndOrganizationId(Integer organizationId);
    
    // Buscar clientes B2B
    List<Client> findByIsBToBTrueAndOrganizationId(Integer organizationId);
    
    // Buscar por tipo de persona (J: Jurídica, N: Natural)
    List<Client> findByPersonTypeAndOrganizationId(String personType, Integer organizationId);
    
    // Buscar por departamento
    List<Client> findByStateNameAndOrganizationId(String stateName, Integer organizationId);
    
    // Buscar por municipio
    List<Client> findByCityNameAndOrganizationId(String cityName, Integer organizationId);
}