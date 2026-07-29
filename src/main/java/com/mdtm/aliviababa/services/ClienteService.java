package com.mdtm.aliviababa.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mdtm.aliviababa.dto.ClientePerfilDto;
import com.mdtm.aliviababa.modelo.ClienteEntity;
import com.mdtm.aliviababa.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    //LEER TODOS LOS REGISTROS
    @Transactional(readOnly = true)
    public List<ClienteEntity> obtenerTodos(){
        return repository.findAll();
    }

    //BUSCAR POR ID
    @Transactional(readOnly = true)
    public ClienteEntity obtenerPorId(Long id){
        return repository.findById(id).orElseThrow(
            () -> new RuntimeException("Cliente no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public ClientePerfilDto obtenerPerfil(String username) {
        ClienteEntity cliente = repository.findByUsuarioUsername(username)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + username));

        return convertirAPerfil(cliente);
    }

    //GUARDAR CLIENTE
    @Transactional
    public ClienteEntity guardarCliente (ClienteEntity cliente){
        return repository.save(cliente);
        //AQUI PUEDEN IR TODAS LAS VALIDACIONES
    }

    //ELIMINAR CLIENTE
    @Transactional
    public void eliminarCliente (Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException("Cliente no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    //ACTUALIZAR CLIENTE
    @Transactional
    public ClienteEntity actualizarCliente (Long id, ClienteEntity detalleClienteEntity){
        ClienteEntity clienteExistente = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Cliente no existe! : " + id));
        
        BeanUtils.copyProperties(detalleClienteEntity, clienteExistente, "id");

        return repository.save(clienteExistente);
    }

    @Transactional
    public ClientePerfilDto actualizarPerfil(String username, ClientePerfilDto detallePerfil) {
        ClienteEntity clienteExistente = repository.findByUsuarioUsername(username)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + username));

        clienteExistente.setNombre(detallePerfil.nombre());
        clienteExistente.setEmail(detallePerfil.email());
        clienteExistente.setDireccion(detallePerfil.direccion());
        clienteExistente.setTelefono(detallePerfil.telefono());

        return convertirAPerfil(repository.save(clienteExistente));
    }

    private ClientePerfilDto convertirAPerfil(ClienteEntity cliente) {
        return new ClientePerfilDto(
            cliente.getUsuario().getUsername(),
            cliente.getNombre(),
            cliente.getEmail(),
            cliente.getDireccion(),
            cliente.getTelefono()
        );
    }

}
