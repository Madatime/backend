package com.mdtm.aliviababa.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mdtm.aliviababa.dto.ActualizarUsuarioPerfilRequest;
import com.mdtm.aliviababa.dto.RegistroRequest;
import com.mdtm.aliviababa.dto.UsuarioPerfilDto;
import com.mdtm.aliviababa.modelo.ClienteEntity;
import com.mdtm.aliviababa.modelo.Rol;
import com.mdtm.aliviababa.modelo.UsuarioEntity;
import com.mdtm.aliviababa.repository.ClienteRepository;
import com.mdtm.aliviababa.repository.UsuarioRepository;

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

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

        String email = request.getEmail().trim();
        if (usuarioRepository.existsByEmail(email)
                || clienteRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "El correo ya está registrado"
            );
        }

        Rol rol;
        try {
            rol = Rol.valueOf(request.getRol());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("El rol seleccionado no es válido");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setRol(rol);

        if (rol == Rol.ROLE_ADMIN) {
            usuario.setEmail(email);
            usuario.setDireccion(request.getDireccion());
            usuario.setTelefono(request.getTelefono());
        }

        UsuarioEntity usuarioGuardado = usuarioRepository.save(usuario);

        if (rol == Rol.ROLE_CLIENTE) {
            ClienteEntity cliente = new ClienteEntity();

            cliente.setNombre(request.getNombre());
            cliente.setEmail(email);
            cliente.setDireccion(request.getDireccion());
            cliente.setTelefono(request.getTelefono());
            cliente.setUsuario(usuarioGuardado);

            clienteRepository.save(cliente);
        }

        return usuarioGuardado;
    }

    @Transactional
    public UsuarioPerfilDto registrarDesdePanel(RegistroRequest request) {
        return convertirAPerfil(saveUsuario(request));
    }

    @Transactional(readOnly = true)
    public UsuarioEntity obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(
                        "Usuario no encontrado: " + username
                ));
    }

    @Transactional(readOnly = true)
    public UsuarioPerfilDto obtenerPerfil(String username) {
        return convertirAPerfil(obtenerPorUsername(username));
    }

    @Transactional
    public UsuarioPerfilDto actualizarPerfil(
            String username,
            ActualizarUsuarioPerfilRequest detallePerfil
    ) {
        UsuarioEntity usuario = obtenerPorUsername(username);

        if (detallePerfil.nombre() == null || detallePerfil.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if (detallePerfil.email() == null || detallePerfil.email().isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio");
        }

        String email = detallePerfil.email().trim();
        if (!email.equalsIgnoreCase(usuario.getEmail())
                && (usuarioRepository.existsByEmailAndIdNot(email, usuario.getId())
                || clienteRepository.existsByEmail(email))) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        usuario.setNombre(detallePerfil.nombre().trim());
        usuario.setEmail(email);
        usuario.setDireccion(detallePerfil.direccion());
        usuario.setTelefono(detallePerfil.telefono());

        if (detallePerfil.password() != null
                && !detallePerfil.password().isBlank()) {
            if (detallePerfil.password().length() < 6) {
                throw new IllegalArgumentException(
                        "La contraseña debe tener al menos 6 caracteres"
                );
            }
            usuario.setPassword(passwordEncoder.encode(detallePerfil.password()));
        }

        return convertirAPerfil(usuarioRepository.save(usuario));
    }

    private UsuarioPerfilDto convertirAPerfil(UsuarioEntity usuario) {
        return new UsuarioPerfilDto(
                usuario.getUsername(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getDireccion(),
                usuario.getTelefono()
        );
    }
}
