package com.mdtm.aliviababa.controller;

import com.mdtm.aliviababa.services.ProcesarVenta;

import java.security.Principal;
import java.util.List;

import org.apache.catalina.connector.Response;
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

import com.mdtm.aliviababa.modelo.VentaEntity;
import com.mdtm.aliviababa.services.VentaService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/venta") //MAPEO GENERAL VENTAS
@CrossOrigin(origins = "http://localhost:5173") //PERMISO A REACT
@RequiredArgsConstructor
public class VentaController {

    private final ProcesarVenta procesarVenta;
    private final VentaService servicio;
    //private final ProcesarVenta servicioVenta;

    //VentaController(ProcesarVenta procesarVenta) {
    //    this.procesarVenta = procesarVenta;
    //}

    @GetMapping("/")
    public ResponseEntity<List<VentaEntity>> listar() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaEntity> obtenerDetallesEntity(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    //eliminar por id
    @DeleteMapping("/{id}")
    public ResponseEntity<VentaEntity> eliminar(@PathVariable Long id){
        servicio.eliminarVenta(id);
        return ResponseEntity.noContent().build(); //204
    }

    //AGREGAR
    @PostMapping("/admin")
    public ResponseEntity<VentaEntity> crear(@RequestBody VentaEntity venta) {
        VentaEntity nuevo = servicio.guardarVenta(venta);
        return new ResponseEntity<> (nuevo, HttpStatus.CREATED);//201 CREATED

    }
    //ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody VentaEntity producto) {
        try{
        VentaEntity ProductoAct = servicio.actualizarVenta(id, producto);
        return ResponseEntity.ok(ProductoAct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
    
    @PostMapping("/")
    public ResponseEntity<?> crearVenta(@RequestBody VentaEntity venta, Principal principal) {
        try{
            String username = principal.getName();
            VentaEntity nuevaVenta = servicio.procesarVenta(venta, username);
            return ResponseEntity.ok(nuevaVenta);
        }catch(Exception ex){
            return ResponseEntity.badRequest().body(ex.getMessage());    
        
        }   
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<List<VentaEntity>> listarMisCompras(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(servicio.obtenerVentasPorCliente(username));
    }
    
}
