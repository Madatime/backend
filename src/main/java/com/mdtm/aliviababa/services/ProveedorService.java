package com.mdtm.aliviababa.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mdtm.aliviababa.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import com.mdtm.aliviababa.modelo.ProveedorEntity;

@Service
@RequiredArgsConstructor
public class ProveedorService {
    private final ProveedorRepository repository;
    
    //LEER TODOS LOS REGISTROS
    @Transactional(readOnly = true)
    public List<ProveedorEntity> obtenerTodos(){
        return repository.findAll();
    }

    //BUSCAR POR ID
    @Transactional(readOnly = true)
    public ProveedorEntity obtenerPorId(Long id){
        return repository.findById(id).orElseThrow(
            () -> new RuntimeException("Proveedor no encontrado: " + id));
    }

    //GUARDAR PROVEEDOR
    @Transactional
    public ProveedorEntity guardarProveedor (ProveedorEntity proveedor){
        return repository.save(proveedor);
        //AQUI PUEDEN IR TODAS LAS VALIDACIONES
    }

    //ELIMINAR PROVEEDOR
    @Transactional
    public void eliminarProveedor (Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException("Proveedor no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    //ACTUALIZAR PROVEEDOR
    @Transactional
    public ProveedorEntity actualizarProveedor (Long id, ProveedorEntity detalleProveedorEntity){
        ProveedorEntity proveedorExistente = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Proveedor no existe! : " + id));
        
        BeanUtils.copyProperties(detalleProveedorEntity, proveedorExistente, "id");

        return repository.save(proveedorExistente);
    }

}
