package com.pos.backend.controller;

import com.pos.backend.dto.LoginRequest;
import com.pos.backend.dto.AuthResponse;
import com.pos.backend.service.AuthService;
import com.pos.backend.repository.RoleRepository;
import com.pos.backend.entity.Usuario;
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
    
    @Autowired
    private RoleRepository roleRepository;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("DEBUG - Intento de login para email: " + loginRequest.getEmail());
            
            AuthResponse response = authService.login(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
            );
            
            System.out.println("DEBUG - Login exitoso, token generado");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("DEBUG - Error en login: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        try {
            String nombre = (String) request.get("nombre");
            String apellido = (String) request.get("apellido");
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            Integer roleId = (Integer) request.get("roleId");
            Integer organizationId = (Integer) request.get("organizationId");
            
            // Validaciones
            if (nombre == null || apellido == null || email == null || password == null || roleId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", true, "message", "Todos los campos son requeridos"));
            }
            
            // Obtener el rol
            var role = roleRepository.findById(roleId);
            if (role.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", true, "message", "Rol no encontrado"));
            }
            
            Usuario nuevoUsuario = authService.crearUsuario(
                    nombre, 
                    apellido, 
                    email, 
                    password, 
                    role.get(), 
                    organizationId
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario creado exitosamente");
            response.put("userId", nuevoUsuario.getId());
            response.put("email", nuevoUsuario.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", true, "message", e.getMessage()));
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
                    String email = authService.getEmailFromToken(token);
                    response.put("email", email);
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
                    String email = authService.getEmailFromToken(token);
                    var usuario = authService.buscarPorEmail(email);
                    
                    if (usuario.isPresent()) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("email", usuario.get().getEmail());
                        response.put("nombre", usuario.get().getNombre());
                        response.put("apellido", usuario.get().getApellido());
                        response.put("role", usuario.get().getRole());
                        
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