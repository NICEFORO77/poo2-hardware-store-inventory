package com.niceforo.inventario.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.niceforo.inventario.dto.DashboardSummaryResponse;
import com.niceforo.inventario.model.EstadoProducto;
import com.niceforo.inventario.repository.CategoriaRepository;
import com.niceforo.inventario.repository.CompraRepository;
import com.niceforo.inventario.repository.MovimientoInventarioRepository;
import com.niceforo.inventario.repository.ProductoRepository;
import com.niceforo.inventario.repository.ProveedorRepository;
import com.niceforo.inventario.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final CompraRepository compraRepository;
    private final VentaRepository ventaRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public DashboardSummaryResponse getSummary() {
        var productos = productoRepository.findAll();

        BigDecimal valorCosto = productos.stream()
                .map(producto -> producto.getPrecioCompra().multiply(BigDecimal.valueOf(producto.getStockActual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorVenta = productos.stream()
                .map(producto -> producto.getPrecioVenta().multiply(BigDecimal.valueOf(producto.getStockActual())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DashboardSummaryResponse.MovimientoResumen> recientes = movimientoInventarioRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(movimiento -> movimiento.getFecha(), Comparator.reverseOrder()))
                .limit(8)
                .map(movimiento -> new DashboardSummaryResponse.MovimientoResumen(
                        movimiento.getId(),
                        movimiento.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        movimiento.getProducto().getNombre(),
                        movimiento.getTipoMovimiento().name(),
                        movimiento.getCantidad(),
                        movimiento.getMotivo()
                ))
                .toList();

        return new DashboardSummaryResponse(
                productoRepository.count(),
                productoRepository.countByEstado(EstadoProducto.ACTIVO),
                productos.stream().filter(producto -> producto.getStockActual() <= producto.getStockMinimo()).count(),
                categoriaRepository.count(),
                proveedorRepository.count(),
                ventaRepository.count(),
                compraRepository.count(),
                valorCosto,
                valorVenta,
                recientes
        );
    }
}
