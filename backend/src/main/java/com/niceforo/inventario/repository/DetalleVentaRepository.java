package com.niceforo.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niceforo.inventario.model.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByVentaId(Long ventaId);

    void deleteByVentaId(Long ventaId);
}
