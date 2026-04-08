package com.niceforo.inventario.dto;

public record AuthResponse(
        String token,
        String tipo,
        String username,
        String nombreCompleto,
        String rol
) {
}
