package com.Nexus_Fashion.inventario_service.controller;


import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Nexus_Fashion.inventario_service.dto.InventarioDTO;
import com.Nexus_Fashion.inventario_service.model.Inventario;
import com.Nexus_Fashion.inventario_service.service.InventarioService;

@RestController
@RequestMapping("/inventarios")
public class InventarioController {

    private final InventarioService inventarioService;
    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @PostMapping
    public ResponseEntity<InventarioDTO> crearInventario(@Valid @RequestBody InventarioDTO inventarioDto) {
        try {
            logger.info("POST /inventarios - Creando inventario: idProducto={}, sku={}", 
                    inventarioDto.getIdProducto(), inventarioDto.getSku());
            
            // Conversión manual de DTO a Entidad como lo hace el profesor
            Inventario nuevoInventario = Inventario.builder()
                    .idProducto(inventarioDto.getIdProducto())
                    .sku(inventarioDto.getSku())
                    .cantidadDisponible(inventarioDto.getCantidadDisponible())
                    .ubicacionBodega(inventarioDto.getUbicacionBodega())
                    .build();

            Inventario guardado = inventarioService.guardar(nuevoInventario);
            logger.info("Inventario creado exitosamente id={}", guardado.getId());
            
            return ResponseEntity.ok(mapearADto(guardado));
        } catch (Exception e) {
            logger.error("Error al crear inventario: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<InventarioDTO>> listarInventarios() {
        logger.info("GET /inventarios - Listando inventarios");
        List<Inventario> inventarios = inventarioService.listar();
        
        List<InventarioDTO> dtos = inventarios.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
                
        logger.info("Total inventarios listados: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> obtenerInventario(@PathVariable Long id) {
        logger.info("GET /inventarios/{} - Obteniendo inventario", id);
        try {
            Inventario inventario = inventarioService.obtenerPorId(id);
            logger.info("Inventario obtenido id={}", id);
            return ResponseEntity.ok(mapearADto(inventario));
        } catch (Exception e) {
            logger.error("Error al obtener inventario id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventarioDTO> actualizarInventario(@PathVariable Long id, @Valid @RequestBody InventarioDTO inventarioDto) {
        logger.info("PUT /inventarios/{} - Actualizando inventario", id);
        try {
            Inventario existente = inventarioService.obtenerPorId(id);
            existente.setIdProducto(inventarioDto.getIdProducto());
            existente.setSku(inventarioDto.getSku());
            existente.setCantidadDisponible(inventarioDto.getCantidadDisponible());
            existente.setUbicacionBodega(inventarioDto.getUbicacionBodega());

            Inventario actualizado = inventarioService.guardar(existente);
            logger.info("Inventario actualizado exitosamente id={}", id);
            return ResponseEntity.ok(mapearADto(actualizado));
        } catch (Exception e) {
            logger.error("Error al actualizar inventario id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarInventario(@PathVariable Long id) {
        logger.info("DELETE /inventarios/{} - Eliminando inventario", id);
        try {
            inventarioService.eliminar(id);
            logger.info("Inventario eliminado exitosamente id={}", id);
            return ResponseEntity.ok("Inventario Eliminado Exitosamente");
        } catch (Exception e) {
            logger.error("Error al eliminar inventario id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    // Método helper para transformar la entidad física en DTO
    private InventarioDTO mapearADto(Inventario inventario) {
        return InventarioDTO.builder()
                .id(inventario.getId())
                .idProducto(inventario.getIdProducto())
                .sku(inventario.getSku())
                .cantidadDisponible(inventario.getCantidadDisponible())
                .ubicacionBodega(inventario.getUbicacionBodega())
                .build();
    }
}
