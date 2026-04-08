package com.niceforo.inventario.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record VentaRequest(
        LocalDateTime fecha,
        String cliente,
        @Valid
        @NotEmpty(message = "Debe agregar al menos un producto")
        List<VentaItemRequest> detalles
) {
}
