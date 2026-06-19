package com.Nexus_Fashion.venta_service.controller;

import com.Nexus_Fashion.venta_service.dto.VentaDTO;
import com.Nexus_Fashion.venta_service.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<VentaDTO> crearVenta(@Valid @RequestBody VentaDTO dto) {
        logger.info("POST /ventas - Intentando registrar una nueva transacción de venta");
        
        VentaDTO guardado = ventaService.guardar(dto);
        
        logger.info("POST /ventas - Venta procesada y almacenada con éxito");
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping
    public ResponseEntity<List<VentaDTO>> listarVentas() {
        logger.info("GET /ventas - Solicitando el historial completo de ventas registradas");
        
        List<VentaDTO> ventas = ventaService.listar();
        
        logger.info("GET /ventas - Historial entregado. Cantidad de registros encontrados: {}", ventas.size());
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> obtenerVenta(@PathVariable Long id) {
        logger.info("GET /ventas/{} - Buscando comprobante de venta por ID", id);
        
        VentaDTO dto = ventaService.buscarPorId(id);
        if (dto == null) {
            logger.warn("GET /ventas/{} - No se encontró ningún registro de venta con el ID solicitado", id);
            return ResponseEntity.notFound().build();
        }
        
        logger.info("GET /ventas/{} - Datos de la venta recuperados exitosamente", id);
        return ResponseEntity.ok(dto);
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