package com.mdtm.aliviababa.services;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mdtm.aliviababa.modelo.ClienteEntity;
import com.mdtm.aliviababa.modelo.DetalleVentaEntity;
import com.mdtm.aliviababa.modelo.ProductoEntity;
import com.mdtm.aliviababa.modelo.VentaEntity;
import com.mdtm.aliviababa.repository.ClienteRepository;
import com.mdtm.aliviababa.repository.ProductoRepository;
import com.mdtm.aliviababa.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<VentaEntity> obtenerTodos() {
        return ventaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public VentaEntity obtenerPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    @Transactional
    public VentaEntity guardarVenta(VentaEntity venta) {
        return ventaRepository.save(venta);
    }

    @Transactional
    public void eliminarVenta(Long id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada");
        }

        ventaRepository.deleteById(id);
    }

    @Transactional
    public VentaEntity actualizarVenta(Long id, VentaEntity detalleVenta) {
        VentaEntity ventaExistente = ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        BeanUtils.copyProperties(
                detalleVenta,
                ventaExistente,
                "id",
                "detalles",
                "cliente",
                "fecha"
        );

        return ventaRepository.save(ventaExistente);
    }

    @Transactional
    public VentaEntity procesarVenta(VentaEntity ventaRequest, String username) {
        ClienteEntity cliente = clienteRepository.findByUsuarioUsername(username)
                .orElseThrow(() -> new RuntimeException("Cliente no registrado: " + username));

        ventaRequest.setCliente(cliente);
        ventaRequest.setFecha(LocalDateTime.now());
        ventaRequest.setEstadoPago("PENDIENTE");

        double total = 0.0;

        for (DetalleVentaEntity detalle : ventaRequest.getDetalles()) {

            ProductoEntity producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto no existe"));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente del producto: " + producto.getNombre());
            }

            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);

            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio() * detalle.getCantidad());
            detalle.setVenta(ventaRequest);

            total += detalle.getSubtotal();
        }

        ventaRequest.setTotal(total);

        return ventaRepository.save(ventaRequest);
    }

    @Transactional
    public VentaEntity confirmarPago(Long idVenta) {
        VentaEntity venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + idVenta));

        venta.setEstadoPago("PAGADO");

        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public List<VentaEntity> obtenerVentasPorCliente(String username) {
        return ventaRepository.findByClienteUsuarioUsername(username);
    }
}
