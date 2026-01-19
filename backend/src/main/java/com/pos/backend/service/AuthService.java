package com.pos.backend.service;

import com.pos.backend.entity.Role;
import com.pos.backend.entity.Usuario;
import com.pos.backend.repository.UsuarioRepository;
import com.pos.backend.repository.RoleRepository;
import com.pos.backend.util.JwtUtil;
import com.pos.backend.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Transactional(readOnly = true)
    public AuthResponse login(String email, String password) throws Exception {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        
        if (usuario.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }
        
        Usuario user = usuario.get();
        
        // Verificar que el usuario esté activo
        if (!user.getActivo()) {
            throw new Exception("Usuario desactivado");
        }
        
        // Verificar contraseña usando el password_hash
        // Primero intentar con BCrypt, si falla intentar texto plano (compatibilidad)
        boolean passwordValid = false;
        
        try {
            // Intentar con BCrypt primero
            passwordValid = passwordEncoder.matches(password, user.getPasswordHash());
        } catch (Exception e) {
            // Si BCrypt falla, verificar si es texto plano (para compatibilidad)
            passwordValid = password.equals(user.getPasswordHash());
        }
        
        // Si ninguna de las dos funcionó, verificar texto plano directamente
        if (!passwordValid) {
            passwordValid = password.equals(user.getPasswordHash());
        }
        
        if (!passwordValid) {
            System.out.println("DEBUG - Login fallido para: " + email);
            System.out.println("DEBUG - Password enviado: " + password);
            System.out.println("DEBUG - Password en BD: " + user.getPasswordHash());
            throw new Exception("Credenciales incorrectas");
        }
        
        System.out.println("DEBUG - Login exitoso para: " + email);
        
        // Cargar el role directamente del repositorio para evitar lazy loading
        System.out.println("DEBUG - Role ID del usuario: " + (user.getRole() != null ? user.getRole().getId() : "null"));
        
        Role role;
        try {
            role = user.getRole();
            if (role != null) {
                System.out.println("DEBUG - Role encontrado: " + role.getNombre());
            } else {
                System.out.println("DEBUG - Role NO encontrado para ID: " + (user.getRole() != null ? user.getRole().getId() : "null"));
            }
        } catch (Exception e) {
            System.out.println("DEBUG - Error cargando role: " + e.getMessage());
            throw new Exception("Error al cargar información del rol del usuario");
        }
        
        if (role == null) {
            throw new Exception("El rol del usuario no fue encontrado en la base de datos");
        }
        
        AuthResponse.RoleDTO roleDTO = new AuthResponse.RoleDTO(
                role.getId(),
                role.getNombre(),
                role.getDescripcion()
        );
        
        String token = jwtUtil.generateToken(email);
        
        return new AuthResponse(
                token,
                "Bearer",
                user.getEmail(),
                user.getNombre(),
                user.getApellido(),
                roleDTO
        );
    }
    
    @Transactional
    public Usuario crearUsuario(String nombre, String apellido, String email, String password, Role role, Integer organizationId) throws Exception {
        if (usuarioRepository.existsByEmail(email)) {
            throw new Exception("Ya existe un usuario con este email");
        }
        
        // Validar que la contraseña tenga al menos 6 caracteres
        if (password.length() < 6) {
            throw new Exception("La contraseña debe tener al menos 6 caracteres");
        }
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setOrganizationId(organizationId != null ? organizationId : 1); // Default organization
        nuevoUsuario.setUsername(email);   // Use email as username
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setEmail(email);
        
        // Encriptar la contraseña antes de guardar
        String hashedPassword = passwordEncoder.encode(password);
        nuevoUsuario.setPasswordHash(hashedPassword);
        
        nuevoUsuario.setRole(role);
        nuevoUsuario.setActivo(true);
        
        return usuarioRepository.save(nuevoUsuario);
    }
    
    // Método sobrecargado para compatibilidad
    public Usuario crearUsuario(String nombre, String apellido, String email, String password, Role role) throws Exception {
        return crearUsuario(nombre, apellido, email, password, role, 1);
    }
    
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    public boolean validarToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    public String getEmailFromToken(String token) {
        return jwtUtil.getCorreoFromJWT(token);
    }
}