package com.mdtm.aliviababa.dto;

public record ActualizarUsuarioPerfilRequest(
        String nombre,
        String email,
        String direccion,
        String telefono,
        String password
) {
}
