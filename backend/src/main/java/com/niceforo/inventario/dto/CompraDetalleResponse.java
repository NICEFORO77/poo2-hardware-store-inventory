package com.niceforo.inventario.dto;

import java.math.BigDecimal;

public record CompraDetalleResponse(
        Long idDetalleCompra,
        Long idProducto,
        String codigoProducto,
        String producto,
        Integer cantidad,
        BigDecimal precioCompra,
        BigDecimal subtotal
) {
}
