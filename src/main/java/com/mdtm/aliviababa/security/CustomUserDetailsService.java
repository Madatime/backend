package com.mdtm.aliviababa.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mdtm.aliviababa.modelo.ClienteEntity;
import com.mdtm.aliviababa.modelo.UsuarioEntity;
import com.mdtm.aliviababa.repository.ClienteRepository;
import com.mdtm.aliviababa.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository, ClienteRepository clienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
    }

     @Override
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        UsuarioEntity usuario;

        ClienteEntity cliente = clienteRepository
                .findByEmail(correo)
                .orElse(null);

        if (cliente != null) {
            usuario = cliente.getUsuario();
        } else {
            usuario = usuarioRepository.findByUsername(correo)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "Usuario o correo no encontrado"
                            )
                    );
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(usuario.getRol().name())
                .build();
    }

}
