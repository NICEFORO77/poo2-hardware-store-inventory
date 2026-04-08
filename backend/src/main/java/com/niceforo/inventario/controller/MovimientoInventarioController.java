package com.niceforo.inventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niceforo.inventario.dto.MovimientoRequest;
import com.niceforo.inventario.model.MovimientoInventario;
import com.niceforo.inventario.service.MovimientoInventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    @GetMapping
    public List<MovimientoInventario> findAll() {
        return movimientoInventarioService.findAll();
    }

    @GetMapping("/{id}")
    public MovimientoInventario findById(@PathVariable Long id) {
        return movimientoInventarioService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MovimientoInventario> create(@Validated @RequestBody MovimientoRequest request) {
        return ResponseEntity.ok(movimientoInventarioService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoInventario> update(@PathVariable Long id, @Validated @RequestBody MovimientoRequest request) {
        return ResponseEntity.ok(movimientoInventarioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movimientoInventarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
