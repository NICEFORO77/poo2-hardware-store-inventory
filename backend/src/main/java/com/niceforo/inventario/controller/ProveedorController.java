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

import com.niceforo.inventario.model.Proveedor;
import com.niceforo.inventario.repository.ProveedorRepository;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorRepository proveedorRepository;

    @GetMapping
    public List<Proveedor> findAll() {
        return proveedorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Proveedor findById(@PathVariable Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado"));
    }

    @PostMapping
    public ResponseEntity<Proveedor> create(@RequestBody Proveedor proveedor) {
        return ResponseEntity.ok(proveedorRepository.save(proveedor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> update(@PathVariable Long id, @RequestBody Proveedor request) {
        Proveedor proveedor = findById(id);
        proveedor.setNombre(request.getNombre());
        proveedor.setDocumento(request.getDocumento());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setCorreo(request.getCorreo());
        proveedor.setDireccion(request.getDireccion());
        return ResponseEntity.ok(proveedorRepository.save(proveedor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proveedorRepository.delete(findById(id));
        return ResponseEntity.noContent().build();
    }
}
