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

import com.mdtm.aliviababa.modelo.DetalleVentaEntity;
import com.mdtm.aliviababa.services.DetalleVentaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/detalleVenta") //MAPEO GENERAL DETALLES DE DETALLE DE VENTA
@CrossOrigin(origins = "http://localhost:5173") //PERMISO A REACT
@RequiredArgsConstructor
public class DetalleVentaController {

    private final DetalleVentaService servicio;

    @GetMapping("/")
    public ResponseEntity<List<DetalleVentaEntity>> listar() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentaEntity> obtenerDetallesEntity(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    //eliminar por id
    @DeleteMapping("/{id}")
    public ResponseEntity<DetalleVentaEntity> eliminar(@PathVariable Long id){
        servicio.eliminarDetalleVenta(id);
        return ResponseEntity.noContent().build(); //204
    }

    //AGREGAR
    @PostMapping("/")
    public ResponseEntity<DetalleVentaEntity> crear(@RequestBody DetalleVentaEntity detalle) {
        DetalleVentaEntity nuevo = servicio.guardarDetalleVenta(detalle);
        return new ResponseEntity<> (nuevo, HttpStatus.CREATED);//201 CREATED

    }
    //ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody DetalleVentaEntity detalle) {
        try{
        DetalleVentaEntity DetalleAct = servicio.actualizarDetalleVenta(id, detalle);
        return ResponseEntity.ok(DetalleAct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}
