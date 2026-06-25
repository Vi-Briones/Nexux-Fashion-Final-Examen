package com.Nexus_Fashion.soporte_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Nexus_Fashion.soporte_service.dto.SoporteDTO;
import com.Nexus_Fashion.soporte_service.model.Soporte;
import com.Nexus_Fashion.soporte_service.service.SoporteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/soportes")
public class SoporteController {

    private final SoporteService soporteService;
    private static final Logger logger = LoggerFactory.getLogger(SoporteController.class);

    public SoporteController(SoporteService soporteService) {
        this.soporteService = soporteService;
    }

    @PostMapping
    public ResponseEntity<SoporteDTO> crearSoporte(@Valid @RequestBody SoporteDTO dto) {
        try {
            logger.info("POST /soportes - Creando ticket de soporte: idCliente={}, asunto={}", 
               dto.getIdCliente(), dto.getAsunto());
            
            Soporte nueva = soporteService.guardar(dto.toModel());
            
            logger.info("Ticket de soporte creado exitosamente id={}", nueva.getId());
            return ResponseEntity.ok(SoporteDTO.fromModel(nueva));
        } catch (Exception e) {
            logger.error("Error al crear ticket de soporte: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<SoporteDTO>> listarSoportes() {
        logger.info("GET /soportes - Listando soportes");
        List<Soporte> lista = soporteService.listar();
        
        List<SoporteDTO> dtos = lista.stream()
                .map(SoporteDTO::fromModel)
                .collect(Collectors.toList());
        logger.info("Total tickets de soporte listados: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoporteDTO> obtenerSoportes(@PathVariable Long id) {
        logger.info("GET /soportes/{} - Obteniendo ticket de soporte", id);
        try {
            Soporte soporte = soporteService.obtenerPorId(id);
            logger.info("Ticket de soporte obtenido id={}", id);
            return ResponseEntity.ok(SoporteDTO.fromModel(soporte));
        } catch (Exception e) {
            logger.error("Error al obtener ticket de soporte id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SoporteDTO> actualizarSoporte(@PathVariable Long id, @Valid @RequestBody SoporteDTO dto) {
        logger.info("PUT /soportes/{} - Actualizando ticket de soporte", id);
        try {
            Soporte actualizada = soporteService.actualizar(id, dto.toModel());
            logger.info("Ticket de soporte actualizado exitosamente id={}", id);
            return ResponseEntity.ok(SoporteDTO.fromModel(actualizada));
        } catch (Exception e) {
            logger.error("Error al actualizar ticket de soporte id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarSoporte(@PathVariable Long id) {
        logger.info("DELETE /soportes/{} - Eliminando ticket de soporte", id);
        try {
            soporteService.eliminar(id);
            logger.info("Ticket de soporte eliminado exitosamente id={}", id);
            return ResponseEntity.ok("Ticket de soporte Eliminado Exitosamente");
        } catch (Exception e) {
            logger.error("Error al eliminar notificación id={}: {}", id, e.getMessage());
            throw e;
        }
    }

}
