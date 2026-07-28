package com.mdtm.aliviababa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mdtm.aliviababa.modelo.DetalleVentaEntity;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVentaEntity, Long> {

}
