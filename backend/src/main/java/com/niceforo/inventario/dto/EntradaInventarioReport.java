package com.niceforo.inventario.dto;

import java.math.BigDecimal;

public record EntradaInventarioReport(
        String fecha,
        String producto,
        Integer cantidad,
        BigDecimal precioCompra,
        String proveedor
) {
}
