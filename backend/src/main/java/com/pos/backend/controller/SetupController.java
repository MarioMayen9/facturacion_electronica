package com.pos.backend.controller;

import com.pos.backend.entity.Role;
import com.pos.backend.entity.Usuario;
import com.pos.backend.entity.Organization;
import com.pos.backend.entity.TaxpayerClassification;
import com.pos.backend.repository.RoleRepository;
import com.pos.backend.repository.UsuarioRepository;
import com.pos.backend.repository.OrganizationRepository;
import com.pos.backend.repository.TaxpayerClassificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/setup")
@CrossOrigin(origins = "*")
public class SetupController {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private TaxpayerClassificationRepository taxpayerClassificationRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/init-data")
    public Map<String, Object> initializeData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Crear roles si no existen
            if (roleRepository.count() == 0) {
                Role adminRole = new Role();
                adminRole.setNombre("ADMINISTRADOR");
                adminRole.setDescripcion("Administrador del sistema con acceso completo");
                adminRole.setActivo(true);
                adminRole.setOrganizationId(1);
                adminRole.setCreatedAt(LocalDateTime.now());
                adminRole.setUpdatedAt(LocalDateTime.now());
                adminRole = roleRepository.save(adminRole);
                
                Role cajeroRole = new Role();
                cajeroRole.setNombre("CAJERO");
                cajeroRole.setDescripcion("Cajero con acceso a ventas y consultas básicas");
                cajeroRole.setActivo(true);
                cajeroRole.setOrganizationId(1);
                cajeroRole.setCreatedAt(LocalDateTime.now());
                cajeroRole.setUpdatedAt(LocalDateTime.now());
                roleRepository.save(cajeroRole);
                
                Role vendedorRole = new Role();
                vendedorRole.setNombre("VENDEDOR");
                vendedorRole.setDescripcion("Vendedor con acceso limitado a ventas");
                vendedorRole.setActivo(true);
                vendedorRole.setOrganizationId(1);
                vendedorRole.setCreatedAt(LocalDateTime.now());
                vendedorRole.setUpdatedAt(LocalDateTime.now());
                roleRepository.save(vendedorRole);
                
                // Crear usuario admin si no existe
                if (!usuarioRepository.existsByEmail("admin@pos.com")) {
                    Usuario admin = new Usuario();
                    admin.setUsername("admin@pos.com");
                    admin.setEmail("admin@pos.com");
                    admin.setPasswordHash(passwordEncoder.encode("password123"));
                    admin.setNombre("Admin");
                    admin.setApellido("Sistema");
                    admin.setRole(adminRole);
                    admin.setActivo(true);
                    admin.setOrganizationId(1);
                    admin.setCreatedAt(LocalDateTime.now());
                    admin.setUpdatedAt(LocalDateTime.now());
                    usuarioRepository.save(admin);
                }
                
                response.put("success", true);
                response.put("message", "Datos iniciales creados correctamente");
                response.put("rolesCreated", 3);
                response.put("usersCreated", 1);
            } else {
                response.put("success", true);
                response.put("message", "Los datos ya existen en la base de datos");
                response.put("rolesCount", roleRepository.count());
                response.put("usersCount", usuarioRepository.count());
            }
            
            // Crear clasificaciones de contribuyentes si no existen
            if (taxpayerClassificationRepository.count() == 0) {
                TaxpayerClassification gran = new TaxpayerClassification();
                gran.setCode("GRAN");
                gran.setName("Gran Contribuyente");
                gran.setDescription("Empresas con ingresos mayores a $150,000 anuales");
                gran.setOrganizationId(1);
                gran.setActive(true);
                gran.setCreatedAt(LocalDateTime.now());
                gran.setUpdatedAt(LocalDateTime.now());
                taxpayerClassificationRepository.save(gran);
                
                TaxpayerClassification mediano = new TaxpayerClassification();
                mediano.setCode("MED");
                mediano.setName("Mediano Contribuyente");
                mediano.setDescription("Empresas con ingresos entre $50,000 y $150,000 anuales");
                mediano.setOrganizationId(1);
                mediano.setActive(true);
                mediano.setCreatedAt(LocalDateTime.now());
                mediano.setUpdatedAt(LocalDateTime.now());
                taxpayerClassificationRepository.save(mediano);
                
                TaxpayerClassification pequeno = new TaxpayerClassification();
                pequeno.setCode("PEQ");
                pequeno.setName("Pequeño Contribuyente");
                pequeno.setDescription("Empresas con ingresos menores a $50,000 anuales");
                pequeno.setOrganizationId(1);
                pequeno.setActive(true);
                pequeno.setCreatedAt(LocalDateTime.now());
                pequeno.setUpdatedAt(LocalDateTime.now());
                taxpayerClassificationRepository.save(pequeno);
                
                response.put("taxpayerClassificationsCreated", 3);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    @GetMapping("/check-data")
    public Map<String, Object> checkData() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("rolesCount", roleRepository.count());
        response.put("usersCount", usuarioRepository.count());
        response.put("organizationsCount", organizationRepository.count());
        response.put("taxpayerClassificationsCount", taxpayerClassificationRepository.count());
        
        try {
            // Información detallada para debug
            var allRoles = roleRepository.findAll();
            response.put("allRoles", allRoles);
            
            var allUsers = usuarioRepository.findAll();
            response.put("allUsers", allUsers.stream().map(u -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", u.getId());
                userInfo.put("email", u.getEmail());
                userInfo.put("roleId", u.getRole() != null ? u.getRole().getId() : "NULL");
                userInfo.put("roleName", u.getRole() != null ? u.getRole().getNombre() : "NULL");
                return userInfo;
            }).toList());
            
            var allOrgs = organizationRepository.findAll();
            response.put("allOrganizations", allOrgs);
            
            var allClassifications = taxpayerClassificationRepository.findAll();
            response.put("allTaxpayerClassifications", allClassifications);
            
        } catch (Exception e) {
            response.put("debugError", e.getMessage());
        }
        
        return response;
    }
    
    @PostMapping("/create-test-role")
    public ResponseEntity<?> createTestRole() {
        try {
            // Crear rol ADMINISTRADOR manualmente con ID específico
            Role adminRole = new Role();
            adminRole.setOrganizationId(1);
            adminRole.setNombre("ADMINISTRADOR");
            adminRole.setDescripcion("Administrador del sistema con acceso completo");
            adminRole.setActivo(true);
            adminRole.setCreatedAt(LocalDateTime.now());
            adminRole.setUpdatedAt(LocalDateTime.now());
            
            Role savedRole = roleRepository.save(adminRole);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Rol creado exitosamente");
            result.put("roleId", savedRole.getId());
            result.put("roleName", savedRole.getNombre());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Error al crear rol: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}