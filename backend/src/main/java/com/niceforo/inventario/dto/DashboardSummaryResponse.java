package com.niceforo.inventario.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        long totalProductos,
        long productosActivos,
        long productosBajoStock,
        long totalCategorias,
        long totalProveedores,
        long totalVentas,
        long totalCompras,
        BigDecimal valorInventarioCosto,
        BigDecimal valorInventarioVenta,
        List<MovimientoResumen> movimientosRecientes
) {
    public record MovimientoResumen(
            Long id,
            String fecha,
            String producto,
            String tipo,
            Integer cantidad,
            String motivo
    ) {
    }
}
