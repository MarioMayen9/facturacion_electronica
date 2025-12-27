package com.pos.backend.controller;

import com.pos.backend.dto.LoginRequest;
import com.pos.backend.dto.AuthResponse;
import com.pos.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(
                    loginRequest.getCorreo(), 
                    loginRequest.getPassword()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                boolean isValid = authService.validarToken(token);
                
                Map<String, Object> response = new HashMap<>();
                response.put("valid", isValid);
                
                if (isValid) {
                    String correo = authService.getCorreoFromToken(token);
                    response.put("correo", correo);
                }
                
                return ResponseEntity.ok(response);
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", "Token no válido"));
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("valid", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (authService.validarToken(token)) {
                    String correo = authService.getCorreoFromToken(token);
                    var usuario = authService.buscarPorCorreo(correo);
                    
                    if (usuario.isPresent()) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("correo", usuario.get().getCorreo());
                        response.put("nombre", usuario.get().getNombre());
                        response.put("apellido", usuario.get().getApellido());
                        response.put("rol", usuario.get().getRol());
                        
                        return ResponseEntity.ok(response);
                    }
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token no válido"));
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}