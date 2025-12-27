package com.pos.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String nombre;
    
    @NotBlank
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String apellido;
    
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String correo;
    
    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role rol;
    
    // Constructor vacío
    public Usuario() {}
    
    // Constructor con parámetros
    public Usuario(String nombre, String apellido, String correo, String password, Role rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
    }
    
    // Getters y setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Role getRol() {
        return rol;
    }
    
    public void setRol(Role rol) {
        this.rol = rol;
    }
}