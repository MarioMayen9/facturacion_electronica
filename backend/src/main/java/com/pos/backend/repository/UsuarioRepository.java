package com.pos.backend.repository;

import com.pos.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<Usuario> findByUsername(String username);
    Boolean existsByUsername(String username);
}