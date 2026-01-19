package com.pos.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6)
    private String password;
    
    // Constructor sin parámetros
    public LoginRequest() {}
    
    // Constructor con parámetros
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Getters y setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // Compatibility method
    public String getCorreo() {
        return email;
    }
    
    public void setCorreo(String correo) {
        this.email = correo;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}