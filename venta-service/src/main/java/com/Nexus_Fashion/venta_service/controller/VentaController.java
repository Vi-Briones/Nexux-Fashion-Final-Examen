package com.Nexus_Fashion.venta_service.controller;

import com.Nexus_Fashion.venta_service.assemblers.VentaModelAssembler;
import com.Nexus_Fashion.venta_service.dto.VentaDTO;
import com.Nexus_Fashion.venta_service.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
// ------------------------------------------

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    private final VentaService ventaService;
    private final VentaModelAssembler assembler; // Inyectamos el ensamblador

    // Constructor único para inyección de dependencias
    public VentaController(VentaService ventaService, VentaModelAssembler assembler) {
        this.ventaService = ventaService;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<VentaDTO> crearVenta(@Valid @RequestBody VentaDTO dto) {
        logger.info("POST /ventas - Intentando registrar una nueva transacción de venta");
        VentaDTO guardado = ventaService.guardar(dto);
        logger.info("POST /ventas - Venta procesada y almacenada con éxito");
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // --- MODIFICADO CON HATEOAS: Listar todas ---
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<VentaDTO>>> listarVentas() {
        logger.info("GET /ventas - Solicitando el historial completo de ventas registradas");
        
        List<VentaDTO> ventas = ventaService.listar();
        
        // Mapeamos la lista de DTOs planos a una lista de modelos hipermedia
        List<EntityModel<VentaDTO>> dtosConLinks = ventas.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
                
        logger.info("GET /ventas - Historial entregado. Cantidad de registros encontrados: {}", dtosConLinks.size());
        
        // Creamos la colección final HATEOAS agregando el link propio de la lista
        CollectionModel<EntityModel<VentaDTO>> modeloFinal = CollectionModel.of(dtosConLinks,
                linkTo(methodOn(VentaController.class).listarVentas()).withSelfRel());
                
        return ResponseEntity.ok(modeloFinal);
    }

    // --- MODIFICADO CON HATEOAS: Buscar por ID ---
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<VentaDTO>> obtenerVenta(@PathVariable Long id) {
        logger.info("GET /ventas/{} - Buscando comprobante de venta por ID", id);
        
        VentaDTO dto = ventaService.buscarPorId(id);
        if (dto == null) {
            logger.warn("GET /ventas/{} - No se encontró ningún registro de venta con el ID solicitado", id);
            return ResponseEntity.notFound().build();
        }
        
        logger.info("GET /ventas/{} - Datos de la venta recuperados exitosamente", id);
        
        // Pasamos el DTO al assembler para que le clave las rutas de navegación
        return ResponseEntity.ok(assembler.toModel(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VentaDTO> actualizarVenta(@PathVariable Long id, @Valid @RequestBody VentaDTO ventaDto) {
        logger.info("PUT /ventas/{} - Iniciando actualización", id);
        try {
            VentaDTO actualizada = ventaService.actualizar(id, ventaDto);
            logger.info("PUT /ventas/{} - Venta actualizada exitosamente", id);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            logger.warn("PUT /ventas/{} - Error al actualizar: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        logger.info("DELETE /ventas/{} - Iniciando eliminación", id);
        try {
            ventaService.eliminar(id);
            logger.info("DELETE /ventas/{} - Venta eliminada exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.warn("DELETE /ventas/{} - Error al eliminar: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
