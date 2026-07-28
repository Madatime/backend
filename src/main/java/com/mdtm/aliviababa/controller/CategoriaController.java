package com.mdtm.aliviababa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mdtm.aliviababa.modelo.CategoriaEntity;
import com.mdtm.aliviababa.services.CategoriaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categorias/")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService servicio;

    @GetMapping
    public ResponseEntity<List<CategoriaEntity>> listar() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaEntity> obtenerDetallesEntity(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<CategoriaEntity> crear(@RequestBody CategoriaEntity categoria) {
        CategoriaEntity nuevo = servicio.guardarCategoria(categoria);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody CategoriaEntity categoria) {
        try {
            CategoriaEntity categoriaAct = servicio.actualizarCategoria(id, categoria);
            return ResponseEntity.ok(categoriaAct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}