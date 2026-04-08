package com.niceforo.inventario.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
        Long idVenta,
        LocalDateTime fecha,
        String cliente,
        BigDecimal total,
        List<VentaDetalleResponse> detalles
) {
}
