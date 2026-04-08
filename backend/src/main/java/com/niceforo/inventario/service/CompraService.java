package com.niceforo.inventario.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.niceforo.inventario.dto.CompraDetalleResponse;
import com.niceforo.inventario.dto.CompraItemRequest;
import com.niceforo.inventario.dto.CompraRequest;
import com.niceforo.inventario.dto.CompraResponse;
import com.niceforo.inventario.model.Compra;
import com.niceforo.inventario.model.DetalleCompra;
import com.niceforo.inventario.model.Producto;
import com.niceforo.inventario.model.Proveedor;
import com.niceforo.inventario.model.TipoMovimiento;
import com.niceforo.inventario.repository.CompraRepository;
import com.niceforo.inventario.repository.DetalleCompraRepository;
import com.niceforo.inventario.repository.ProductoRepository;
import com.niceforo.inventario.repository.ProveedorRepository;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioService movimientoInventarioService;

    public List<CompraResponse> findAll() {
        return compraRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Compra::getFecha).reversed())
                .map(this::toResponse)
                .toList();
    }

    public CompraResponse findById(Long id) {
        return toResponse(findCompra(id));
    }

    @Transactional
    public CompraResponse create(CompraRequest request) {
        Compra compra = Compra.builder()
                .fecha(request.fecha() == null ? LocalDateTime.now() : request.fecha())
                .proveedor(findProveedor(request.idProveedor()))
                .total(BigDecimal.ZERO)
                .build();

        compra = compraRepository.save(compra);
        processDetails(compra, request.detalles());
        return toResponse(compraRepository.save(compra));
    }

    @Transactional
    public CompraResponse update(Long id, CompraRequest request) {
        Compra compra = findCompra(id);
        reversePurchase(compra);
        compra.setFecha(request.fecha() == null ? compra.getFecha() : request.fecha());
        compra.setProveedor(findProveedor(request.idProveedor()));
        compra.setTotal(BigDecimal.ZERO);
        processDetails(compra, request.detalles());
        return toResponse(compraRepository.save(compra));
    }

    @Transactional
    public void delete(Long id) {
        Compra compra = findCompra(id);
        reversePurchase(compra);
        compraRepository.delete(compra);
    }

    private void processDetails(Compra compra, List<CompraItemRequest> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        String referencia = buildReference(compra.getId());

        for (CompraItemRequest item : detalles) {
            Producto producto = productoRepository.findById(item.idProducto())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado"));

            BigDecimal subtotal = item.precioCompra().multiply(BigDecimal.valueOf(item.cantidad()));
            total = total.add(subtotal);

            producto.setStockActual(producto.getStockActual() + item.cantidad());
            producto.setPrecioCompra(item.precioCompra());
            producto.setProveedorPrincipal(compra.getProveedor());
            productoRepository.save(producto);

            DetalleCompra detalle = DetalleCompra.builder()
                    .compra(compra)
                    .producto(producto)
                    .cantidad(item.cantidad())
                    .precioCompra(item.precioCompra())
                    .subtotal(subtotal)
                    .build();
            detalleCompraRepository.save(detalle);

            movimientoInventarioService.createSystemMovement(
                    producto,
                    TipoMovimiento.ENTRADA,
                    item.cantidad(),
                    "Ingreso por compra",
                    referencia,
                    compra.getFecha()
            );
        }

        compra.setTotal(total);
    }

    private void reversePurchase(Compra compra) {
        List<DetalleCompra> detalles = detalleCompraRepository.findByCompraId(compra.getId());
        for (DetalleCompra detalle : detalles) {
            Producto producto = detalle.getProducto();
            if (producto.getStockActual() - detalle.getCantidad() < 0) {
                throw new ResponseStatusException(
                        BAD_REQUEST,
                        "No se puede revertir la compra porque el stock actual de " + producto.getNombre() + " ya fue consumido"
                );
            }
            producto.setStockActual(producto.getStockActual() - detalle.getCantidad());
            productoRepository.save(producto);
        }
        detalleCompraRepository.deleteByCompraId(compra.getId());
        movimientoInventarioService.deleteByReference(buildReference(compra.getId()));
    }

    private Compra findCompra(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Compra no encontrada"));
    }

    private Proveedor findProveedor(Long idProveedor) {
        return proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado"));
    }

    private CompraResponse toResponse(Compra compra) {
        List<CompraDetalleResponse> detalles = detalleCompraRepository.findByCompraId(compra.getId())
                .stream()
                .map(detalle -> new CompraDetalleResponse(
                        detalle.getId(),
                        detalle.getProducto().getId(),
                        detalle.getProducto().getCodigo(),
                        detalle.getProducto().getNombre(),
                        detalle.getCantidad(),
                        detalle.getPrecioCompra(),
                        detalle.getSubtotal()
                ))
                .toList();

        return new CompraResponse(
                compra.getId(),
                compra.getFecha(),
                compra.getProveedor().getId(),
                compra.getProveedor().getNombre(),
                compra.getTotal(),
                detalles
        );
    }

    private String buildReference(Long compraId) {
        return "COMPRA-" + compraId;
    }
}
