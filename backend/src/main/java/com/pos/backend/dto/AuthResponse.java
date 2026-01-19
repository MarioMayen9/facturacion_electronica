package com.pos.backend.dto;

public class AuthResponse {
    private String token;
    private String tipo;
    private String email;
    private String nombre;
    private String apellido;
    private RoleDTO role;
    
    public static class RoleDTO {
        private Integer id;
        private String name;
        private String description;
        
        public RoleDTO() {}
        
        public RoleDTO(Integer id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
        
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    
    // Constructor sin parámetros
    public AuthResponse() {}
    
    // Constructor con parámetros
    public AuthResponse(String token, String tipo, String email, String nombre, String apellido, RoleDTO role) {
        this.token = token;
        this.tipo = tipo;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.role = role;
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
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
    
    public RoleDTO getRole() {
        return role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }
}