package com.mdtm.aliviababa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mdtm.aliviababa.modelo.ProductoEntity;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {
                                                                
}
