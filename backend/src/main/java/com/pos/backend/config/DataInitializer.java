package com.pos.backend.config;

import com.pos.backend.entity.Role;
import com.pos.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private AuthService authService;
    
    @Override
    public void run(String... args) throws Exception {
        // Crear usuario administrador inicial si no existe
        try {
            if (authService.buscarPorCorreo("mario.mayen.castro@gmail.com").isEmpty()) {
                authService.crearUsuario(
                    "Mario",
                    "Mayen",
                    "mario.mayen.castro@gmail.com",
                    "admin123",
                    Role.ADMIN
                );
                System.out.println("Usuario administrador creado: mario.mayen.castro@gmail.com");
            } else {
                System.out.println("Usuario administrador ya existe");
            }
        } catch (Exception e) {
            System.err.println("Error al crear usuario inicial: " + e.getMessage());
        }
    }
}