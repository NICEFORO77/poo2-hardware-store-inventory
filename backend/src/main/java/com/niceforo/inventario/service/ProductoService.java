package com.niceforo.inventario.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.niceforo.inventario.dto.ProductoRequest;
import com.niceforo.inventario.model.Categoria;
import com.niceforo.inventario.model.Producto;
import com.niceforo.inventario.model.Proveedor;
import com.niceforo.inventario.repository.CategoriaRepository;
import com.niceforo.inventario.repository.ProductoRepository;
import com.niceforo.inventario.repository.ProveedorRepository;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;
    private final StorageService storageService;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto findById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado"));
    }

    public Producto create(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new ResponseStatusException(BAD_REQUEST, "El codigo ya existe");
        }
        Producto producto = new Producto();
        mapFields(producto, request);
        return productoRepository.save(producto);
    }

    public Producto update(Long id, ProductoRequest request) {
        Producto producto = findById(id);
        if (!producto.getCodigo().equals(request.getCodigo()) && productoRepository.existsByCodigo(request.getCodigo())) {
            throw new ResponseStatusException(BAD_REQUEST, "El codigo ya existe");
        }
        mapFields(producto, request);
        return productoRepository.save(producto);
    }

    public void delete(Long id) {
        productoRepository.delete(findById(id));
    }

    private void mapFields(Producto producto, ProductoRequest request) {
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoria no encontrada"));

        Proveedor proveedorPrincipal = null;
        if (request.getIdProveedorPrincipal() != null) {
            proveedorPrincipal = proveedorRepository.findById(request.getIdProveedorPrincipal())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor principal no encontrado"));
        }

        if (request.getStockActual() != null && request.getStockActual() < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "El stock actual no puede ser negativo");
        }
        if (request.getStockMinimo() == null || request.getStockMinimo() < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "El stock minimo es obligatorio");
        }

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setUnidadMedida(request.getUnidadMedida());
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setStockActual(request.getStockActual() == null ? 0 : request.getStockActual());
        producto.setStockMinimo(request.getStockMinimo());
        producto.setEstado(request.getEstado());
        producto.setCategoria(categoria);
        producto.setProveedorPrincipal(proveedorPrincipal);
        if (request.getFoto() != null && !request.getFoto().isEmpty()) {
            producto.setFotoUrl(storageService.store(request.getFoto()));
        } else if (request.getFotoBase64() != null && !request.getFotoBase64().isBlank()) {
            producto.setFotoUrl(storageService.storeBase64(request.getFotoBase64(), request.getFotoNombre()));
        }
    }
}
