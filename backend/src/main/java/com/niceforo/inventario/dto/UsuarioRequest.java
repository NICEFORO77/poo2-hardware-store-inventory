package com.niceforo.inventario.dto;

import com.niceforo.inventario.model.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombreCompleto,
        @NotBlank(message = "El username es obligatorio")
        String username,
        String password,
        @Email(message = "El correo no es valido")
        @NotBlank(message = "El correo es obligatorio")
        String correo,
        @NotNull(message = "El rol es obligatorio")
        Role rol,
        Boolean activo
) {
}
