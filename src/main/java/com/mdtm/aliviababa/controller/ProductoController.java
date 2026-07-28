package com.mdtm.aliviababa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mdtm.aliviababa.modelo.ProductoEntity;
import com.mdtm.aliviababa.services.ProductoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService servicio;

    @GetMapping
    public ResponseEntity<List<ProductoEntity>> listar() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoEntity> obtenerDetallesEntity(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ProductoEntity> crear(@RequestBody ProductoEntity producto) {
        ProductoEntity nuevo = servicio.guardarProducto(producto);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ProductoEntity producto) {
        try {
            ProductoEntity productoAct = servicio.actualizarProducto(id, producto);
            return ResponseEntity.ok(productoAct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}