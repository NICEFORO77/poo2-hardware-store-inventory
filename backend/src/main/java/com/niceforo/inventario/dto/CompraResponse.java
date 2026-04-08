package com.niceforo.inventario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraResponse(
        Long idCompra,
        LocalDateTime fecha,
        Long idProveedor,
        String proveedor,
        BigDecimal total,
        List<CompraDetalleResponse> detalles
) {
}
