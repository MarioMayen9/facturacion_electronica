package com.pos.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "password123";
        String hashedPassword = encoder.encode(password);
        
        System.out.println("Password: " + password);
        System.out.println("Hashed: " + hashedPassword);
        System.out.println("Verification: " + encoder.matches(password, hashedPassword));
        
        // Generar hashes para diferentes usuarios
        System.out.println("\n=== HASHES PARA USUARIOS ===");
        System.out.println("Admin: " + encoder.encode("admin123"));
        System.out.println("Cajero: " + encoder.encode("cajero123"));  
        System.out.println("Vendedor: " + encoder.encode("vendedor123"));
        System.out.println("Supervisor: " + encoder.encode("supervisor123"));
    }
}