package com.pos.backend.dto;

import com.pos.backend.entity.Role;

public class AuthResponse {
    private String token;
    private String tipo;
    private String correo;
    private String nombre;
    private String apellido;
    private Role rol;
    
    // Constructor sin parámetros
    public AuthResponse() {}
    
    // Constructor con parámetros
    public AuthResponse(String token, String tipo, String correo, String nombre, String apellido, Role rol) {
        this.token = token;
        this.tipo = tipo;
        this.correo = correo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
    }
    
    // Getters y setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public Role getRol() {
        return rol;
    }
    
    public void setRol(Role rol) {
        this.rol = rol;
    }
}