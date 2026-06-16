package com.Nexus_Fashion.venta_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Nexus_Fashion.venta_service.model.Venta;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    // Buscar ventas por cliente
    List<Venta> findByIdCliente(Long idCliente);
    
    // Contar ventas de un cliente
    long countByIdCliente(Long idCliente);
}
