package com.pos.backend.repository;

import com.pos.backend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    @Query("SELECT r FROM Role r WHERE r.organizationId = :organizationId AND r.activo = true")
    List<Role> findByOrganizationIdAndActivo(@Param("organizationId") Integer organizationId);
    
    Optional<Role> findByNombreAndOrganizationId(String nombre, Integer organizationId);
    
    List<Role> findByOrganizationId(Integer organizationId);
    
    @Query("SELECT r FROM Role r WHERE r.activo = true")
    List<Role> findAllActive();
    
    // Método simple sin filtro de organización para resolver el problema del login
    @Query("SELECT r FROM Role r WHERE r.id = :id")
    Optional<Role> findByIdSimple(@Param("id") Integer id);
    
    Boolean existsByNombreAndOrganizationId(String nombre, Integer organizationId);
}