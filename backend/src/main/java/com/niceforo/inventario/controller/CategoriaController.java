package com.niceforo.inventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.niceforo.inventario.model.Categoria;
import com.niceforo.inventario.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Categoria findById(@PathVariable Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoria no encontrada"));
    }

    @PostMapping
    public ResponseEntity<Categoria> create(@RequestBody Categoria categoria) {
        return ResponseEntity.ok(categoriaRepository.save(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable Long id, @RequestBody Categoria request) {
        Categoria categoria = findById(id);
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        return ResponseEntity.ok(categoriaRepository.save(categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaRepository.delete(findById(id));
        return ResponseEntity.noContent().build();
    }
}
