package com.pos.backend.service;

import com.pos.backend.entity.Role;
import com.pos.backend.entity.Usuario;
import com.pos.backend.repository.UsuarioRepository;
import com.pos.backend.util.JwtUtil;
import com.pos.backend.dto.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public AuthResponse login(String correo, String password) throws Exception {
        Optional<Usuario> usuario = usuarioRepository.findByCorreo(correo);
        
        if (usuario.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }
        
        Usuario user = usuario.get();
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Credenciales incorrectas");
        }
        
        String token = jwtUtil.generateToken(correo);
        
        return new AuthResponse(
                token,
                "Bearer",
                user.getCorreo(),
                user.getNombre(),
                user.getApellido(),
                user.getRol()
        );
    }
    
    public Usuario crearUsuario(String nombre, String apellido, String correo, String password, Role rol) throws Exception {
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new Exception("Ya existe un usuario con este correo");
        }
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setPassword(passwordEncoder.encode(password));
        nuevoUsuario.setRol(rol);
        
        return usuarioRepository.save(nuevoUsuario);
    }
    
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }
    
    public boolean validarToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    public String getCorreoFromToken(String token) {
        return jwtUtil.getCorreoFromJWT(token);
    }
}