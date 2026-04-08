package com.niceforo.inventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niceforo.inventario.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByCorreo(String correo);
}
