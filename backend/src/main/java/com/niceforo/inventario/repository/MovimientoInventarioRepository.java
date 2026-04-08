package com.niceforo.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niceforo.inventario.model.MovimientoInventario;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByReferencia(String referencia);

    void deleteByReferencia(String referencia);
}
