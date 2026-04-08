package com.niceforo.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niceforo.inventario.model.Compra;

public interface CompraRepository extends JpaRepository<Compra, Long> {
}
