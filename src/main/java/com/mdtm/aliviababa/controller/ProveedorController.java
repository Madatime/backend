package com.mdtm.aliviababa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mdtm.aliviababa.modelo.ProveedorEntity;
import com.mdtm.aliviababa.services.ProveedorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/proveedor") //MAPEO GENERAL PROVVEEDORES
@CrossOrigin(origins = "http://localhost:5173") //PERMISO A REACT
@RequiredArgsConstructor
public class ProveedorController {
    private final ProveedorService servicio;

    @GetMapping("/")
    public ResponseEntity<List<ProveedorEntity>> listar() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorEntity> obtenerDetallesEntity(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    //eliminar por id
    @DeleteMapping("/{id}")
    public ResponseEntity<ProveedorEntity> eliminar(@PathVariable Long id){
        servicio.eliminarProveedor(id);
        return ResponseEntity.noContent().build(); //204
    }

    //AGREGAR
    @PostMapping("/")
    public ResponseEntity<ProveedorEntity> crear(@RequestBody ProveedorEntity proveedor) {
        ProveedorEntity nuevo = servicio.guardarProveedor(proveedor);
        return new ResponseEntity<> (nuevo, HttpStatus.CREATED);//201 CREATED

    }
    //ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ProveedorEntity producto) {
        try{
        ProveedorEntity ProductoAct = servicio.actualizarProveedor(id, producto);
        return ResponseEntity.ok(ProductoAct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }


}
