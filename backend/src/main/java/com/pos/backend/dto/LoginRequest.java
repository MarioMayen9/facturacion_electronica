package com.pos.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @NotBlank
    @Email
    private String correo;
    
    @NotBlank
    @Size(min = 6)
    private String password;
    
    // Constructor sin parámetros
    public LoginRequest() {}
    
    // Constructor con parámetros
    public LoginRequest(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }
    
    // Getters y setters
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}