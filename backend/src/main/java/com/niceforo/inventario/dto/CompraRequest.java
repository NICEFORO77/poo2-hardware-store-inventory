package com.niceforo.inventario.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CompraRequest(
        LocalDateTime fecha,
        @NotNull(message = "El proveedor es obligatorio")
        Long idProveedor,
        @Valid
        @NotEmpty(message = "Debe agregar al menos un producto")
        List<CompraItemRequest> detalles
) {
}
