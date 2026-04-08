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

import com.niceforo.inventario.dto.VentaRequest;
import com.niceforo.inventario.dto.VentaResponse;
import com.niceforo.inventario.service.VentaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    public List<VentaResponse> findAll() {
        return ventaService.findAll();
    }

    @GetMapping("/{id}")
    public VentaResponse findById(@PathVariable Long id) {
        return ventaService.findById(id);
    }

    @PostMapping
    public ResponseEntity<VentaResponse> create(@Validated @RequestBody VentaRequest request) {
        return ResponseEntity.ok(ventaService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaResponse> update(@PathVariable Long id, @Validated @RequestBody VentaRequest request) {
        return ResponseEntity.ok(ventaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ventaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
