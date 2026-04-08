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

import com.niceforo.inventario.dto.CompraRequest;
import com.niceforo.inventario.dto.CompraResponse;
import com.niceforo.inventario.service.CompraService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    public List<CompraResponse> findAll() {
        return compraService.findAll();
    }

    @GetMapping("/{id}")
    public CompraResponse findById(@PathVariable Long id) {
        return compraService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CompraResponse> create(@Validated @RequestBody CompraRequest request) {
        return ResponseEntity.ok(compraService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompraResponse> update(@PathVariable Long id, @Validated @RequestBody CompraRequest request) {
        return ResponseEntity.ok(compraService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
