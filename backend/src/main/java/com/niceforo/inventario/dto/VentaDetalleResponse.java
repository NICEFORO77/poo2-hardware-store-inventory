package com.niceforo.inventario.dto;

import java.math.BigDecimal;

public record VentaDetalleResponse(
        Long idDetalleVenta,
        Long idProducto,
        String codigoProducto,
        String producto,
        Integer cantidad,
        BigDecimal precioVenta,
        BigDecimal subtotal
) {
}
