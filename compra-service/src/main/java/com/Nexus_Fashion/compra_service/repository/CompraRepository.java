package com.Nexus_Fashion.compra_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Nexus_Fashion.compra_service.model.Compra;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long>{
    List<Compra> findByIdCliente(Long idCliente);
    long countByIdCliente(Long idCliente);
}
