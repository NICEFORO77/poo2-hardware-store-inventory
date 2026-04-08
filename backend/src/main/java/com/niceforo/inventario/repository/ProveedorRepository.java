package com.niceforo.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niceforo.inventario.model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}
