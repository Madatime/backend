package com.mdtm.aliviababa.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "detalleventa")
@Data
public class DetalleVentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

    private Double precioUnitario;

    private Double subtotal;

    //---------------- RELACIONES ----------------

    @ManyToOne
    @JoinColumn(name = "venta_id")
    private VentaEntity venta;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private ProductoEntity producto;

}