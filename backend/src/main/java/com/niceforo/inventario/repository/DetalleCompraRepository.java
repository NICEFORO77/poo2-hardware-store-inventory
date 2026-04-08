package com.niceforo.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niceforo.inventario.model.DetalleCompra;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {

    List<DetalleCompra> findByCompraId(Long compraId);

    void deleteByCompraId(Long compraId);
}
