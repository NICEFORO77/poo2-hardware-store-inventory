package com.niceforo.inventario.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.niceforo.inventario.dto.MovimientoRequest;
import com.niceforo.inventario.model.MovimientoInventario;
import com.niceforo.inventario.model.Producto;
import com.niceforo.inventario.model.TipoMovimiento;
import com.niceforo.inventario.repository.MovimientoInventarioRepository;
import com.niceforo.inventario.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;
    private final ProductoRepository productoRepository;

    public List<MovimientoInventario> findAll() {
        return movimientoInventarioRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(MovimientoInventario::getFecha).reversed())
                .toList();
    }

    public MovimientoInventario findById(Long id) {
        return movimientoInventarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Movimiento no encontrado"));
    }

    @Transactional
    public MovimientoInventario create(MovimientoRequest request) {
        Producto producto = findProducto(request.idProducto());
        applyStockChange(producto, request.tipoMovimiento(), request.cantidad());
        productoRepository.save(producto);

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .fecha(request.fecha() == null ? LocalDateTime.now() : request.fecha())
                .producto(producto)
                .tipoMovimiento(request.tipoMovimiento())
                .cantidad(request.cantidad())
                .motivo(request.motivo())
                .referencia(request.referencia())
                .build();

        return movimientoInventarioRepository.save(movimiento);
    }

    @Transactional
    public MovimientoInventario update(Long id, MovimientoRequest request) {
        MovimientoInventario current = findById(id);
        Producto productoAnterior = current.getProducto();
        revertStockChange(productoAnterior, current.getTipoMovimiento(), current.getCantidad());
        productoRepository.save(productoAnterior);

        Producto productoNuevo = findProducto(request.idProducto());
        applyStockChange(productoNuevo, request.tipoMovimiento(), request.cantidad());
        productoRepository.save(productoNuevo);

        current.setFecha(request.fecha() == null ? current.getFecha() : request.fecha());
        current.setProducto(productoNuevo);
        current.setTipoMovimiento(request.tipoMovimiento());
        current.setCantidad(request.cantidad());
        current.setMotivo(request.motivo());
        current.setReferencia(request.referencia());
        return movimientoInventarioRepository.save(current);
    }

    @Transactional
    public void delete(Long id) {
        MovimientoInventario movimiento = findById(id);
        revertStockChange(movimiento.getProducto(), movimiento.getTipoMovimiento(), movimiento.getCantidad());
        productoRepository.save(movimiento.getProducto());
        movimientoInventarioRepository.delete(movimiento);
    }

    @Transactional
    public void createSystemMovement(
            Producto producto,
            TipoMovimiento tipoMovimiento,
            Integer cantidad,
            String motivo,
            String referencia,
            LocalDateTime fecha
    ) {
        MovimientoInventario movimiento = MovimientoInventario.builder()
                .fecha(fecha)
                .producto(producto)
                .tipoMovimiento(tipoMovimiento)
                .cantidad(cantidad)
                .motivo(motivo)
                .referencia(referencia)
                .build();
        movimientoInventarioRepository.save(movimiento);
    }

    @Transactional
    public void deleteByReference(String referencia) {
        movimientoInventarioRepository.deleteByReferencia(referencia);
    }

    private Producto findProducto(Long idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado"));
    }

    private void applyStockChange(Producto producto, TipoMovimiento tipoMovimiento, Integer cantidad) {
        int delta = resolveDelta(tipoMovimiento, cantidad);
        int updatedStock = producto.getStockActual() + delta;
        if (updatedStock < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Stock insuficiente para " + producto.getNombre());
        }
        producto.setStockActual(updatedStock);
    }

    private void revertStockChange(Producto producto, TipoMovimiento tipoMovimiento, Integer cantidad) {
        int delta = resolveDelta(tipoMovimiento, cantidad);
        int updatedStock = producto.getStockActual() - delta;
        if (updatedStock < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "No se puede revertir el movimiento por stock insuficiente");
        }
        producto.setStockActual(updatedStock);
    }

    private int resolveDelta(TipoMovimiento tipoMovimiento, Integer cantidad) {
        if (cantidad == null || cantidad == 0) {
            throw new ResponseStatusException(BAD_REQUEST, "La cantidad no puede ser cero");
        }

        return switch (tipoMovimiento) {
            case ENTRADA -> {
                if (cantidad < 0) {
                    throw new ResponseStatusException(BAD_REQUEST, "La cantidad en una entrada debe ser positiva");
                }
                yield cantidad;
            }
            case SALIDA -> {
                if (cantidad < 0) {
                    throw new ResponseStatusException(BAD_REQUEST, "La cantidad en una salida debe ser positiva");
                }
                yield -cantidad;
            }
            case AJUSTE -> cantidad;
        };
    }
}
