package com.Nexus_Fashion.inventario_service.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Nexus_Fashion.inventario_service.model.Inventario;
import com.Nexus_Fashion.inventario_service.repository.InventarioRepository;

@Service
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private static final Logger logger = LoggerFactory.getLogger(InventarioService.class);

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    // 1. OBTENER TODO EL INVENTARIO
    public List<Inventario> listar() {
        logger.info("Service: Consultando el stock global de la bodega en Nexus Fashion");
        return inventarioRepository.findAll();
    }

    // 2. OBTENER POR ID
    public Inventario obtenerPorId(Long id) {
        logger.info("Service: Buscando registro de inventario con ID: {}", id);
        return inventarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Service: No se encontró el registro de inventario con ID: {}", id);
                    return new RuntimeException("Registro de inventario no encontrado");
                });
    }

    // 3. REGISTRAR O ACTUALIZAR UN PRODUCTO EN EL INVENTARIO (CRUD)
    @Transactional
    public Inventario guardar(Inventario inventario) {
        logger.info("Service: Registrando/Actualizando producto con SKU: {} en el inventario", inventario.getSku());
        
        // Validación de regla de negocio elemental (No tener stock negativo)
        if (inventario.getCantidadDisponible() < 0) {
            logger.warn("Service: Intento de registrar stock negativo para el SKU: {}", inventario.getSku());
            throw new IllegalArgumentException("La cantidad disponible no puede ser menor a cero");
        }
        
        return inventarioRepository.save(inventario);
    }

    // 4. ELIMINAR UN REGISTRO DEL INVENTARIO (CRUD)
    @Transactional
    public void eliminar(Long id) {
        logger.info("Service: Solicitando eliminación del registro de inventario ID: {}", id);
        Inventario inventario = obtenerPorId(id);
        inventarioRepository.delete(inventario);
        logger.info("Service: Registro ID: {} eliminado correctamente", id);
    }

    // 5. REGLA DE NEGOCIO CRÍTICA: VALIDAR Y DESCONTAR STOCK (Para cuando compren)
    @Transactional
    public boolean validarYDescontarStock(Long idProducto, Integer cantidadSolicitada) {
        logger.info("Service: Validando existencias para Producto ID: {}. Cantidad requerida: {}", idProducto, cantidadSolicitada);
        
        // Buscamos el producto en el inventario
        Inventario inventario = inventarioRepository.findByIdProducto(idProducto)
                .orElseThrow(() -> {
                    logger.error("Service: El Producto ID: {} no existe en la tabla de inventarios", idProducto);
                    return new RuntimeException("El producto solicitado no está registrado en el inventario");
                });

        // Aplicamos la regla de negocio: ¿Hay stock suficiente?
        if (inventario.getCantidadDisponible() < cantidadSolicitada) {
            logger.warn("Service: Stock insuficiente para Producto ID: {}. Disponible: {}, Solicitado: {}", 
                    idProducto, inventario.getCantidadDisponible(), cantidadSolicitada);
            return false; // No hay suficiente stock
        }

        // Si hay stock, lo descontamos
        int nuevoStock = inventario.getCantidadDisponible() - cantidadSolicitada;
        inventario.setCantidadDisponible(nuevoStock);
        inventarioRepository.save(inventario);
        
        logger.info("Service: ¡Descuento de stock exitoso! Producto ID: {}. Nuevo stock restante en bodega: {}", idProducto, nuevoStock);
        return true;
    }
}
