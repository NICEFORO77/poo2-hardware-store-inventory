package com.niceforo.inventario.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.niceforo.inventario.dto.CompraReport;
import com.niceforo.inventario.dto.EntradaInventarioReport;
import com.niceforo.inventario.dto.ProductoMasVendidoReport;
import com.niceforo.inventario.dto.SalidaInventarioReport;
import com.niceforo.inventario.dto.StockActualReport;
import com.niceforo.inventario.dto.StockBajoReport;
import com.niceforo.inventario.dto.UtilidadBasicaReport;
import com.niceforo.inventario.dto.ValorizacionInventarioReport;
import com.niceforo.inventario.dto.VentaReport;
import com.niceforo.inventario.model.DetalleVenta;
import com.niceforo.inventario.model.TipoMovimiento;
import com.niceforo.inventario.repository.DetalleCompraRepository;
import com.niceforo.inventario.repository.DetalleVentaRepository;
import com.niceforo.inventario.repository.MovimientoInventarioRepository;
import com.niceforo.inventario.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReporteService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<StockActualReport> stockActual() {
        return productoRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(producto -> producto.getNombre().toLowerCase()))
                .map(producto -> new StockActualReport(
                        producto.getCodigo(),
                        producto.getNombre(),
                        producto.getCategoria().getNombre(),
                        producto.getStockActual(),
                        producto.getStockMinimo(),
                        producto.getPrecioVenta()
                ))
                .toList();
    }

    public List<StockBajoReport> stockBajo() {
        return productoRepository.findAll()
                .stream()
                .filter(producto -> producto.getStockActual() <= producto.getStockMinimo())
                .sorted(Comparator.comparingInt(producto -> producto.getStockActual()))
                .map(producto -> new StockBajoReport(
                        producto.getNombre(),
                        producto.getStockActual(),
                        producto.getStockMinimo(),
                        producto.getProveedorPrincipal() == null ? "Sin proveedor asignado" : producto.getProveedorPrincipal().getNombre()
                ))
                .toList();
    }

    public List<EntradaInventarioReport> entradasInventario() {
        return detalleCompraRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(detalle -> detalle.getCompra().getFecha(), Comparator.reverseOrder()))
                .map(detalle -> new EntradaInventarioReport(
                        detalle.getCompra().getFecha().format(FORMATTER),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioCompra(),
                        detalle.getCompra().getProveedor().getNombre()
                ))
                .toList();
    }

    public List<SalidaInventarioReport> salidasInventario() {
        return movimientoInventarioRepository.findAll()
                .stream()
                .filter(movimiento -> movimiento.getTipoMovimiento() == TipoMovimiento.SALIDA
                        || (movimiento.getTipoMovimiento() == TipoMovimiento.AJUSTE && movimiento.getCantidad() < 0))
                .sorted(Comparator.comparing(movimiento -> movimiento.getFecha(), Comparator.reverseOrder()))
                .map(movimiento -> new SalidaInventarioReport(
                        movimiento.getFecha().format(FORMATTER),
                        movimiento.getProducto().getNombre(),
                        movimiento.getCantidad(),
                        movimiento.getMotivo()
                ))
                .toList();
    }

    public List<CompraReport> compras() {
        return detalleCompraRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(detalle -> detalle.getCompra().getFecha(), Comparator.reverseOrder()))
                .map(detalle -> new CompraReport(
                        detalle.getCompra().getFecha().format(FORMATTER),
                        detalle.getCompra().getProveedor().getNombre(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getSubtotal()
                ))
                .toList();
    }

    public List<VentaReport> ventas() {
        return detalleVentaRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(detalle -> detalle.getVenta().getFecha(), Comparator.reverseOrder()))
                .map(detalle -> new VentaReport(
                        detalle.getVenta().getFecha().format(FORMATTER),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioVenta(),
                        detalle.getSubtotal()
                ))
                .toList();
    }

    public List<ProductoMasVendidoReport> productosMasVendidos() {
        Map<String, List<DetalleVenta>> grouped = detalleVentaRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(detalle -> detalle.getProducto().getNombre()));

        return grouped.entrySet()
                .stream()
                .map(entry -> {
                    int cantidad = entry.getValue().stream().mapToInt(DetalleVenta::getCantidad).sum();
                    BigDecimal total = entry.getValue().stream()
                            .map(DetalleVenta::getSubtotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new ProductoMasVendidoReport(entry.getKey(), total, cantidad);
                })
                .sorted(Comparator.comparing(ProductoMasVendidoReport::cantidadVendida).reversed())
                .toList();
    }

    public List<ValorizacionInventarioReport> valorizacionInventario() {
        return productoRepository.findAll()
                .stream()
                .map(producto -> new ValorizacionInventarioReport(
                        producto.getNombre(),
                        producto.getStockActual(),
                        producto.getPrecioCompra(),
                        producto.getPrecioVenta(),
                        producto.getPrecioCompra().multiply(BigDecimal.valueOf(producto.getStockActual())),
                        producto.getPrecioVenta().multiply(BigDecimal.valueOf(producto.getStockActual()))
                ))
                .toList();
    }

    public List<UtilidadBasicaReport> utilidadBasica() {
        return productoRepository.findAll()
                .stream()
                .map(producto -> new UtilidadBasicaReport(
                        producto.getNombre(),
                        producto.getPrecioCompra(),
                        producto.getPrecioVenta(),
                        producto.getPrecioVenta().subtract(producto.getPrecioCompra())
                ))
                .toList();
    }

    public Object getReportByKey(String key) {
        return switch (normalizeKey(key)) {
            case "stockActual" -> stockActual();
            case "stockBajo" -> stockBajo();
            case "entradasInventario" -> entradasInventario();
            case "salidasInventario" -> salidasInventario();
            case "compras" -> compras();
            case "ventas" -> ventas();
            case "productosMasVendidos" -> productosMasVendidos();
            case "valorizacionInventario" -> valorizacionInventario();
            case "utilidadBasica" -> utilidadBasica();
            default -> throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Reporte no encontrado");
        };
    }

    public String getReportTitle(String key) {
        return switch (normalizeKey(key)) {
            case "stockActual" -> "Reporte de stock actual";
            case "stockBajo" -> "Reporte de productos con stock bajo";
            case "entradasInventario" -> "Reporte de entradas de inventario";
            case "salidasInventario" -> "Reporte de salidas de inventario";
            case "compras" -> "Reporte de compras";
            case "ventas" -> "Reporte de ventas";
            case "productosMasVendidos" -> "Reporte de productos mas vendidos";
            case "valorizacionInventario" -> "Reporte de valorizacion de inventario";
            case "utilidadBasica" -> "Reporte de utilidad basica";
            default -> throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Reporte no encontrado");
        };
    }

    private String normalizeKey(String key) {
        return key == null ? "" : key.trim();
    }
}
