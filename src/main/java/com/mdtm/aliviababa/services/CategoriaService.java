package com.mdtm.aliviababa.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mdtm.aliviababa.modelo.CategoriaEntity;
import com.mdtm.aliviababa.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    //LEER TODOS LOS REGISTROS
    @Transactional(readOnly = true)
    public List<CategoriaEntity> obtenerTodos(){
        return repository.findAll();
    }

    //BUSCAR POR ID
    @Transactional(readOnly = true)
    public CategoriaEntity obtenerPorId(Long id){
        return repository.findById(id).orElseThrow(
            () -> new RuntimeException("Categoría no encontrada: " + id));
    }

    //GUARDAR CATEGORIA
    @Transactional
    public CategoriaEntity guardarCategoria (CategoriaEntity categoria){
        return repository.save(categoria);
        //AQUI PUEDEN IR TODAS LAS VALIDACIONES
    }

    //ELIMINAR CATEGORIA
    @Transactional
    public void eliminarCategoria (Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException("Categoría no encontrada: " + id);
        }
        repository.deleteById(id);
    }

    //ACTUALIZAR CATEGORIA
    @Transactional
    public CategoriaEntity actualizarCategoria (Long id, CategoriaEntity detalleCategoriaEntity){
        CategoriaEntity categoriaExistente = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Categoría no existe! : " + id));
        
        BeanUtils.copyProperties(detalleCategoriaEntity, categoriaExistente, "id");

        return repository.save(categoriaExistente);
    }

}
