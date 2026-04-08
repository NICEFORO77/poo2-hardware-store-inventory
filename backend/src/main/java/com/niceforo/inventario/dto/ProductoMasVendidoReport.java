package com.niceforo.inventario.dto;

import java.math.BigDecimal;

public record ProductoMasVendidoReport(
        String producto,
        BigDecimal totalVendido,
        Integer cantidadVendida
) {
}
