package com.mdtm.aliviababa.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mdtm.aliviababa.dto.RegistroRequest;
import com.mdtm.aliviababa.modelo.ClienteEntity;
import com.mdtm.aliviababa.modelo.Rol;
import com.mdtm.aliviababa.modelo.UsuarioEntity;
import com.mdtm.aliviababa.repository.ClienteRepository;
import com.mdtm.aliviababa.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
public UsuarioEntity saveUsuario(RegistroRequest request) {

    if (usuarioRepository.existsByUsername(request.getUsername())) {
        throw new IllegalArgumentException(
                "El nombre de usuario ya está registrado"
        );
    }

    if (clienteRepository.existsByEmail(request.getEmail())) {
        throw new IllegalArgumentException(
                "El correo ya está registrado"
        );
    }

    Rol rol = Rol.valueOf(request.getRol());

    UsuarioEntity usuario = new UsuarioEntity();
    usuario.setUsername(request.getUsername());
    usuario.setPassword(passwordEncoder.encode(request.getPassword()));
    usuario.setNombre(request.getNombre());
    usuario.setRol(rol);

    UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

    if (rol == Rol.ROLE_CLIENTE) {
        ClienteEntity cliente = new ClienteEntity();

        cliente.setNombre(request.getNombre());
        cliente.setEmail(request.getEmail());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setUsuario(usuarioGuardado);

        clienteRepository.save(cliente);
    }

    return usuarioGuardado;
}
}