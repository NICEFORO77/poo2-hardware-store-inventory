package com.niceforo.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niceforo.inventario.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}
