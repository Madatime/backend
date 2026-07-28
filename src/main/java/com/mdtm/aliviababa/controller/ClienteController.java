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

import com.mdtm.aliviababa.modelo.ClienteEntity;
import com.mdtm.aliviababa.services.ClienteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cliente") //MAPEO GENERAL CLIENTES
@CrossOrigin(origins = "http://localhost:5173") //PERMISO A REACT
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService servicio;

    @GetMapping("/")
    public ResponseEntity<List<ClienteEntity>> listar() {
        return ResponseEntity.ok(servicio.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteEntity> obtenerDetallesEntity(@PathVariable Long id) {
        return ResponseEntity.ok(servicio.obtenerPorId(id));
    }

    //eliminar por id
    @DeleteMapping("/{id}")
    public ResponseEntity<ClienteEntity> eliminar(@PathVariable Long id){
        servicio.eliminarCliente(id);
        return ResponseEntity.noContent().build(); //204
    }

    //AGREGAR
    @PostMapping("/")
    public ResponseEntity<ClienteEntity> crear(@RequestBody ClienteEntity cliente) {
        ClienteEntity nuevo = servicio.guardarCliente(cliente);
        return new ResponseEntity<> (nuevo, HttpStatus.CREATED);//201 CREATED

    }
    //ACTUALIZAR
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ClienteEntity cliente) {
        try{
        ClienteEntity ClienteAct = servicio.actualizarCliente(id, cliente);
        return ResponseEntity.ok(ClienteAct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}
