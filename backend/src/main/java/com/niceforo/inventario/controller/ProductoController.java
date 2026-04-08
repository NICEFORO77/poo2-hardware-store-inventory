package com.niceforo.inventario.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niceforo.inventario.dto.ProductoRequest;
import com.niceforo.inventario.model.Producto;
import com.niceforo.inventario.service.ProductoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public List<Producto> findAll() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    public Producto findById(@PathVariable Long id) {
        return productoService.findById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Producto> createMultipart(@ModelAttribute ProductoRequest request) {
        return ResponseEntity.ok(productoService.create(request));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Producto> createJson(@RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.create(request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Producto> updateMultipart(@PathVariable Long id, @ModelAttribute ProductoRequest request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Producto> updateJson(@PathVariable Long id, @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
