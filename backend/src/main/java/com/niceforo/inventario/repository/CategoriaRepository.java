package com.niceforo.inventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.niceforo.inventario.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);
}
