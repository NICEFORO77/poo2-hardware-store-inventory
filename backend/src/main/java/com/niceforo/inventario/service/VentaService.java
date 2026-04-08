package com.niceforo.inventario.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.niceforo.inventario.dto.VentaDetalleResponse;
import com.niceforo.inventario.dto.VentaItemRequest;
import com.niceforo.inventario.dto.VentaRequest;
import com.niceforo.inventario.dto.VentaResponse;
import com.niceforo.inventario.model.DetalleVenta;
import com.niceforo.inventario.model.Producto;
import com.niceforo.inventario.model.TipoMovimiento;
import com.niceforo.inventario.model.Venta;
import com.niceforo.inventario.repository.DetalleVentaRepository;
import com.niceforo.inventario.repository.ProductoRepository;
import com.niceforo.inventario.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioService movimientoInventarioService;

    public List<VentaResponse> findAll() {
        return ventaRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Venta::getFecha).reversed())
                .map(this::toResponse)
                .toList();
    }

    public VentaResponse findById(Long id) {
        return toResponse(findVenta(id));
    }

    @Transactional
    public VentaResponse create(VentaRequest request) {
        Venta venta = Venta.builder()
                .fecha(request.fecha() == null ? LocalDateTime.now() : request.fecha())
                .cliente(request.cliente())
                .total(BigDecimal.ZERO)
                .build();

        venta = ventaRepository.save(venta);
        processDetails(venta, request.detalles());
        return toResponse(ventaRepository.save(venta));
    }

    @Transactional
    public VentaResponse update(Long id, VentaRequest request) {
        Venta venta = findVenta(id);
        reverseSale(venta);
        venta.setFecha(request.fecha() == null ? venta.getFecha() : request.fecha());
        venta.setCliente(request.cliente());
        venta.setTotal(BigDecimal.ZERO);
        processDetails(venta, request.detalles());
        return toResponse(ventaRepository.save(venta));
    }

    @Transactional
    public void delete(Long id) {
        Venta venta = findVenta(id);
        reverseSale(venta);
        ventaRepository.delete(venta);
    }

    private void processDetails(Venta venta, List<VentaItemRequest> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        String referencia = buildReference(venta.getId());

        for (VentaItemRequest item : detalles) {
            Producto producto = productoRepository.findById(item.idProducto())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado"));

            if (producto.getStockActual() < item.cantidad()) {
                throw new ResponseStatusException(BAD_REQUEST, "Stock insuficiente para " + producto.getNombre());
            }

            BigDecimal subtotal = item.precioVenta().multiply(BigDecimal.valueOf(item.cantidad()));
            total = total.add(subtotal);

            producto.setStockActual(producto.getStockActual() - item.cantidad());
            productoRepository.save(producto);

            DetalleVenta detalle = DetalleVenta.builder()
                    .venta(venta)
                    .producto(producto)
                    .cantidad(item.cantidad())
                    .precioVenta(item.precioVenta())
                    .subtotal(subtotal)
                    .build();
            detalleVentaRepository.save(detalle);

            movimientoInventarioService.createSystemMovement(
                    producto,
                    TipoMovimiento.SALIDA,
                    item.cantidad(),
                    "Salida por venta",
                    referencia,
                    venta.getFecha()
            );
        }

        venta.setTotal(total);
    }

    private void reverseSale(Venta venta) {
        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(venta.getId());
        for (DetalleVenta detalle : detalles) {
            Producto producto = detalle.getProducto();
            producto.setStockActual(producto.getStockActual() + detalle.getCantidad());
            productoRepository.save(producto);
        }
        detalleVentaRepository.deleteByVentaId(venta.getId());
        movimientoInventarioService.deleteByReference(buildReference(venta.getId()));
    }

    private Venta findVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada"));
    }

    private VentaResponse toResponse(Venta venta) {
        List<VentaDetalleResponse> detalles = detalleVentaRepository.findByVentaId(venta.getId())
                .stream()
                .map(detalle -> new VentaDetalleResponse(
                        detalle.getId(),
                        detalle.getProducto().getId(),
                        detalle.getProducto().getCodigo(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioVenta(),
                        detalle.getSubtotal()
                ))
                .toList();

        return new VentaResponse(
                venta.getId(),
                venta.getFecha(),
                venta.getCliente(),
                venta.getTotal(),
                detalles
        );
    }

    private String buildReference(Long ventaId) {
        return "VENTA-" + ventaId;
    }
}
