package com.niceforo.inventario.dto;

import java.time.LocalDateTime;

import com.niceforo.inventario.model.TipoMovimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovimientoRequest(
        LocalDateTime fecha,
        @NotNull(message = "El producto es obligatorio")
        Long idProducto,
        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipoMovimiento,
        @NotNull(message = "La cantidad es obligatoria")
        Integer cantidad,
        @NotBlank(message = "El motivo es obligatorio")
        String motivo,
        String referencia
) {
}
