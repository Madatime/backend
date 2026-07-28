package com.mdtm.aliviababa.services;

import org.springframework.stereotype.Service;

import com.mdtm.aliviababa.modelo.DetalleVentaEntity;
import com.mdtm.aliviababa.modelo.ProductoEntity;
import com.mdtm.aliviababa.modelo.VentaEntity;
import com.mdtm.aliviababa.repository.ProductoRepository;
import com.mdtm.aliviababa.repository.VentaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcesarVenta {
    private final VentaRepository ventaRepository;
    private final ProductoRepository prodRepo;

    @Transactional
    public VentaEntity procesarVenta(VentaEntity ventaRequest) {
        if (ventaRequest.getDetalles() == null || ventaRequest.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío.");
        }

        ventaRequest.setFecha(java.time.LocalDateTime.now());
        ventaRequest.setEstadoPago("PENDIENTE");

        //CALCULAR TOTALES Y DESCONTAR EL STOCK
        double total = 0.0;
        for(DetalleVentaEntity detalle : ventaRequest.getDetalles()){
            if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
                throw new IllegalArgumentException("El producto de la venta es obligatorio.");
            }
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
            }

            ProductoEntity p = prodRepo.findById(detalle.getProducto().getId()).orElseThrow();
            if (p.getStock() < detalle.getCantidad()) {
                throw new IllegalArgumentException(
                    "Stock insuficiente para " + p.getNombre() + ". Disponible: " + p.getStock()
                );
            }

            p.setStock(p.getStock() - detalle.getCantidad()); //ACTUALIZA STOCK
            detalle.setProducto(p);
            detalle.setPrecioUnitario(p.getPrecio());
            detalle.setSubtotal(p.getPrecio()* detalle.getCantidad());
            detalle.setVenta(ventaRequest);
            total += detalle.getSubtotal();
        }
            ventaRequest.setTotal(total);
            return ventaRepository.save(ventaRequest);

    }

}
