package com.mdtm.aliviababa.services;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mdtm.aliviababa.modelo.DetalleVentaEntity;
import com.mdtm.aliviababa.repository.DetalleVentaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DetalleVentaService {

    private final DetalleVentaRepository repository;
    
    //LEER TODOS LOS REGISTROS
    @Transactional(readOnly = true)
    public List<DetalleVentaEntity> obtenerTodos(){
        return repository.findAll();
    }

    //BUSCAR POR ID
    @Transactional(readOnly = true)
    public DetalleVentaEntity obtenerPorId(Long id){
        return repository.findById(id).orElseThrow(
            () -> new RuntimeException("Detalle de venta no encontrado: " + id));
    }

    //GUARDAR DETALLE DE VENTA
    @Transactional
    public DetalleVentaEntity guardarDetalleVenta (DetalleVentaEntity detalleVenta){
        return repository.save(detalleVenta);
        //AQUI PUEDEN IR TODAS LAS VALIDACIONES
    }

    //ELIMINAR PRODUCTO
    @Transactional
    public void eliminarDetalleVenta (Long id){
        if(!repository.existsById(id)){
            throw new RuntimeException("Detalle de venta no encontrado: " + id);
        }
        repository.deleteById(id);
    }

    //ACTUALIZAR DETALLE DE VENTA
    @Transactional
    public DetalleVentaEntity actualizarDetalleVenta (Long id, DetalleVentaEntity detalleVentaEntity){
        DetalleVentaEntity detalleVentaExistente = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Detalle de venta no existe! : " + id));
        
        BeanUtils.copyProperties(detalleVentaEntity, detalleVentaExistente, "id");

        return repository.save(detalleVentaExistente);
    }

}
