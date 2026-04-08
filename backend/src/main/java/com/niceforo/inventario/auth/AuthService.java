package com.niceforo.inventario.auth;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.niceforo.inventario.dto.AuthRequest;
import com.niceforo.inventario.dto.AuthResponse;
import com.niceforo.inventario.model.Usuario;
import com.niceforo.inventario.repository.UsuarioRepository;
import com.niceforo.inventario.security.CustomUserDetailsService;
import com.niceforo.inventario.security.JwtService;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Credenciales invalidas"));

        String token = jwtService.generateToken(
                userDetailsService.loadUserByUsername(usuario.getUsername()),
                Map.of(
                        "rol", usuario.getRol().name(),
                        "nombreCompleto", usuario.getNombreCompleto()
                )
        );

        return new AuthResponse(
                token,
                "Bearer",
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRol().name()
        );
    }
}
