package com.niceforo.inventario.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.niceforo.inventario.dto.UsuarioRequest;
import com.niceforo.inventario.model.Usuario;
import com.niceforo.inventario.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado"));
    }

    public Usuario create(UsuarioRequest request) {
        validateUnique(request, null);
        if (request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "La contrasena es obligatoria");
        }

        Usuario usuario = Usuario.builder()
                .nombreCompleto(request.nombreCompleto())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .correo(request.correo())
                .rol(request.rol())
                .activo(request.activo() == null || request.activo())
                .build();

        return usuarioRepository.save(usuario);
    }

    public Usuario update(Long id, UsuarioRequest request) {
        Usuario usuario = findById(id);
        validateUnique(request, id);
        usuario.setNombreCompleto(request.nombreCompleto());
        usuario.setUsername(request.username());
        usuario.setCorreo(request.correo());
        usuario.setRol(request.rol());
        usuario.setActivo(request.activo() == null || request.activo());
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.password()));
        }
        return usuarioRepository.save(usuario);
    }

    public void delete(Long id) {
        usuarioRepository.delete(findById(id));
    }

    private void validateUnique(UsuarioRequest request, Long currentId) {
        usuarioRepository.findByUsername(request.username())
                .filter(usuario -> !usuario.getId().equals(currentId))
                .ifPresent(usuario -> {
                    throw new ResponseStatusException(BAD_REQUEST, "El username ya existe");
                });

        usuarioRepository.findAll()
                .stream()
                .filter(usuario -> usuario.getCorreo().equalsIgnoreCase(request.correo()))
                .filter(usuario -> !usuario.getId().equals(currentId))
                .findFirst()
                .ifPresent(usuario -> {
                    throw new ResponseStatusException(BAD_REQUEST, "El correo ya existe");
                });
    }
}
