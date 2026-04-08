package com.niceforo.inventario.dto;

import java.math.BigDecimal;

public record VentaReport(
        String fecha,
        String producto,
        Integer cantidad,
        BigDecimal precioVenta,
        BigDecimal total
) {
}
