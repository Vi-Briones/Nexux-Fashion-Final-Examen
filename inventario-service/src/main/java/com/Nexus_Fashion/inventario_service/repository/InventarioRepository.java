package com.Nexus_Fashion.inventario_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Nexus_Fashion.inventario_service.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long>{

    // Busca un registro de stock usando el ID del producto que viene del microservicio "producto-service"
    Optional<Inventario> findByIdProducto(Long idProducto);
    
    // Busca un registro en bodega usando el código SKU único de negocio
    Optional<Inventario> findBySku(String sku);
}
