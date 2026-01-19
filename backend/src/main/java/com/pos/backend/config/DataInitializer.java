package com.pos.backend.config;

import com.pos.backend.entity.Role;
import com.pos.backend.entity.Usuario;
import com.pos.backend.entity.Organization;
import com.pos.backend.repository.RoleRepository;
import com.pos.backend.repository.UsuarioRepository;
import com.pos.backend.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeOrganization();
        initializeUsers();
        System.out.println("DataInitializer ejecutado - datos base creados");
    }
    
    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setId(1);
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrador del sistema");
            roleRepository.save(adminRole);
            
            Role vendedorRole = new Role();
            vendedorRole.setId(2);
            vendedorRole.setName("VENDEDOR");
            vendedorRole.setDescription("Vendedor del sistema");
            roleRepository.save(vendedorRole);
            
            System.out.println("Roles creados: ADMIN, VENDEDOR");
        }
    }
    
    private void initializeOrganization() {
        if (organizationRepository.count() == 0) {
            Organization org = new Organization();
            org.setId(1);
            org.setName("PYMES Demo");
            org.setDescription("Organización de demostración");
            org.setActive(true);
            organizationRepository.save(org);
            
            System.out.println("Organización creada: PYMES Demo");
        }
    }
    
    private void initializeUsers() {
        if (usuarioRepository.count() == 0) {
            Role adminRole = roleRepository.findById(1).orElse(null);
            Role vendedorRole = roleRepository.findById(2).orElse(null);
            
            if (adminRole != null) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setEmail("nuevouser@pos.com");
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setActive(true);
                admin.setRole(adminRole);
                usuarioRepository.save(admin);
                
                System.out.println("Usuario admin creado (password: password123)");
            }
            
            if (vendedorRole != null) {
                Usuario vendedor = new Usuario();
                vendedor.setUsername("vendedor");
                vendedor.setEmail("vendedor@pos.com");
                vendedor.setPassword(passwordEncoder.encode("password123"));
                vendedor.setActive(true);
                vendedor.setRole(vendedorRole);
                usuarioRepository.save(vendedor);
                
                System.out.println("Usuario vendedor creado (password: password123)");
            }
        }
    }
}