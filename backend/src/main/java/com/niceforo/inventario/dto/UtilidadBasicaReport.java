package com.niceforo.inventario.dto;

import java.math.BigDecimal;

public record UtilidadBasicaReport(
        String producto,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal gananciaUnitaria
) {
}
