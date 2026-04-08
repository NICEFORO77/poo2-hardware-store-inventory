package com.niceforo.inventario.dto;

import java.math.BigDecimal;

public record CompraReport(
        String fecha,
        String proveedor,
        String producto,
        Integer cantidad,
        BigDecimal totalCompra
) {
}
