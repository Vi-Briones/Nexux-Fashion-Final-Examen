package com.Nexus_Fashion.producto_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Nexus_Fashion.producto_service.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Método mágico de Spring Boot que usaremos en el Service
    // Sirve para validar que no ingresemos dos productos con el mismo nombre
    boolean existsByNombre(String nombre);
    
}