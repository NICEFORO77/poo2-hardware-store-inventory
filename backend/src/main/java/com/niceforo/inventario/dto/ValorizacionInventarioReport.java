package com.niceforo.inventario.dto;

import java.math.BigDecimal;

public record ValorizacionInventarioReport(
        String producto,
        Integer stockActual,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal valorCosto,
        BigDecimal valorVenta
) {
}
