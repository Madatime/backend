package com.mdtm.aliviababa.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mdtm.aliviababa.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import com.mdtm.aliviababa.modelo.ProductoEntity;

@Service    
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository repository;

    //LEER TODOS LOS REGISTROS
    @Transactional(readOnly = true)
    public List<ProductoEntity> obtenerTodos(){
        return repository.findAll();
    }

    //BUSCAR POR ID
    @Transactional(readOnly = true)
    public ProductoEntity obtenerPorId(Long id){
        return repository.findById(id).orElseThrow(
            () -> new RuntimeException("Producto no encontrado: " + id));
    }

    //GUARDAR PRODUCTO
    @Transactional
    public ProductoEntity guardarProducto (ProductoEntity producto){
        return repository.save(producto);
        //AQUI PUEDEN IR TODAS LAS VALIDACIONES
    }

    //ELIMINAR PRODUCTO
    @Transactional
    public void eliminarProducto (Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException("Producto no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    //ACTUALIZAR PRODUCTO
    @Transactional
    public ProductoEntity actualizarProducto (Long id, ProductoEntity detalleProductoEntity){
        ProductoEntity productoExistente = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Producto no existe! : " + id));
        
        BeanUtils.copyProperties(detalleProductoEntity, productoExistente, "id");

        return repository.save(productoExistente);
    }

}
