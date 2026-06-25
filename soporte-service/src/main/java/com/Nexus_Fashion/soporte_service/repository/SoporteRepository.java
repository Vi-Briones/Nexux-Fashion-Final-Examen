package com.Nexus_Fashion.soporte_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Nexus_Fashion.soporte_service.model.Soporte;

@Repository
public interface SoporteRepository extends JpaRepository<Soporte, Long>{

    List<Soporte> findByIdCliente(Long idCliente);
}
