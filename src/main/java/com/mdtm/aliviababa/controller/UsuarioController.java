package com.mdtm.aliviababa.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mdtm.aliviababa.dto.ActualizarUsuarioPerfilRequest;
import com.mdtm.aliviababa.dto.RegistroRequest;
import com.mdtm.aliviababa.dto.UsuarioPerfilDto;
import com.mdtm.aliviababa.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService servicio;

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfilDto> obtenerPerfil(Principal principal) {
        return ResponseEntity.ok(servicio.obtenerPerfil(principal.getName()));
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(
            Principal principal,
            @RequestBody ActualizarUsuarioPerfilRequest perfil
    ) {
        try {
            return ResponseEntity.ok(
                    servicio.actualizarPerfil(principal.getName(), perfil)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(servicio.registrarDesdePanel(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
