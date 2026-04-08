package com.niceforo.inventario.dto;

import java.math.BigDecimal;

public record StockActualReport(
        String codigo,
        String producto,
        String categoria,
        Integer stockActual,
        Integer stockMinimo,
        BigDecimal precioVenta
) {
}
