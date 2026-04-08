package com.niceforo.inventario.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CompraItemRequest(
        @NotNull(message = "El producto es obligatorio")
        Long idProducto,
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer cantidad,
        @NotNull(message = "El precio de compra es obligatorio")
        BigDecimal precioCompra
) {
}
