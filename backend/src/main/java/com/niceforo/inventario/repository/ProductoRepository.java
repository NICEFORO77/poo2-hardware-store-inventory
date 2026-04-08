package com.niceforo.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niceforo.inventario.model.EstadoProducto;
import com.niceforo.inventario.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigo(String codigo);

    long countByEstado(EstadoProducto estadoProducto);

    List<Producto> findByStockActualLessThanEqual(Integer stockMinimo);
}
