package com.Nexus_Fashion.recomendaciones_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Nexus_Fashion.recomendaciones_service.dto.RecomendacionDTO;
import com.Nexus_Fashion.recomendaciones_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;
import com.Nexus_Fashion.recomendaciones_service.service.RecomendacionService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recomendaciones")
public class RecomendacionController {

    private static final Logger logger = LoggerFactory.getLogger(RecomendacionController.class);

    private final RecomendacionService recomendacionService;

    public RecomendacionController(RecomendacionService recomendacionService) {
        this.recomendacionService = recomendacionService;
    }

    @PostMapping
    public ResponseEntity<RecomendacionDTO> crearRecomendacion(@Valid @RequestBody RecomendacionDTO recomendacionDto) {
        logger.info("POST /recomendaciones - idCliente={}, idProducto={}",
                recomendacionDto.getIdCliente(), recomendacionDto.getIdProducto());

        Recomendacion nueva = recomendacionService.guardar(recomendacionDto.toModel());

        logger.info("Recomendación creada exitosamente id={}", nueva.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(RecomendacionDTO.fromModel(nueva));
    }

    @GetMapping
    public ResponseEntity<List<RecomendacionDTO>> listarRecomendaciones() {
        logger.info("GET /recomendaciones");

        List<Recomendacion> recomendaciones = recomendacionService.listar();

        logger.debug("Cantidad de recomendaciones obtenidas: {}", recomendaciones.size());

        List<RecomendacionDTO> dtos = recomendaciones.stream()
                .map(RecomendacionDTO::fromModel)
                .collect(Collectors.toList());

        logger.info("GET /recomendaciones - Se retornaron {} recomendaciones exitosamente", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecomendacionDTO> obtenerRecomendacion(@PathVariable Long id) {
        logger.info("GET /recomendaciones/{} - Buscando recomendación por ID", id);

        Recomendacion recomendacion = recomendacionService.buscarPorId(id);
        if (recomendacion == null) {
            logger.warn("GET /recomendaciones/{} - No se encontró la recomendación", id);
            return ResponseEntity.notFound().build();
        }

        logger.info("GET /recomendaciones/{} - Recomendación recuperada exitosamente", id);
        return ResponseEntity.ok(RecomendacionDTO.fromModel(recomendacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecomendacionDTO> actualizarRecomendacion(@PathVariable Long id, @Valid @RequestBody RecomendacionDTO recomendacionDto) {
        logger.info("PUT /recomendaciones/{} - Iniciando actualización", id);

        try {
            Recomendacion actualizada = recomendacionService.actualizar(id, recomendacionDto.toModel());
            logger.info("PUT /recomendaciones/{} - Recomendación actualizada exitosamente", id);
            return ResponseEntity.ok(RecomendacionDTO.fromModel(actualizada));
        } catch (ResourceNotFoundException e) {
            logger.warn("PUT /recomendaciones/{} - Recomendación no encontrada: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRecomendacion(@PathVariable Long id) {
        logger.info("DELETE /recomendaciones/{} - Iniciando eliminación", id);

        try {
            recomendacionService.eliminar(id);
            logger.info("DELETE /recomendaciones/{} - Recomendación eliminada exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            logger.warn("DELETE /recomendaciones/{} - Recomendación no encontrada: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<RecomendacionDTO>> listarRecomendacionesPorCliente(@PathVariable Long idCliente) {
        logger.info("GET /recomendaciones/cliente/{} - Listando recomendaciones por cliente", idCliente);

        List<Recomendacion> recomendaciones = recomendacionService.listarPorCliente(idCliente);
        List<RecomendacionDTO> dtos = recomendaciones.stream()
                .map(RecomendacionDTO::fromModel)
                .collect(Collectors.toList());

        logger.info("GET /recomendaciones/cliente/{} - Se encontraron {} recomendaciones", idCliente, dtos.size());
        return ResponseEntity.ok(dtos);
    }
}

