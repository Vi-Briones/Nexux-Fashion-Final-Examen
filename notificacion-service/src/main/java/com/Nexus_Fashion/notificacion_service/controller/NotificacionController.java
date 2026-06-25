package com.Nexus_Fashion.notificacion_service.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Nexus_Fashion.notificacion_service.dto.NotificacionDTO;
import com.Nexus_Fashion.notificacion_service.model.Notificacion;
import com.Nexus_Fashion.notificacion_service.service.NotificacionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private static final Logger logger = LoggerFactory.getLogger(NotificacionController.class);

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @PostMapping
    public ResponseEntity<NotificacionDTO> crearNotificacion(@Valid @RequestBody NotificacionDTO dto) {
        try {
            logger.info("POST /notificaciones - Creando notificación: idUsuario={}, tipoEvento={}", 
                dto.getIdUsuario(), dto.getTipoEvento());
            
            Notificacion nueva = notificacionService.guardar(dto.toModel());
            
            logger.info("Notificación creada exitosamente id={}", nueva.getId());
            return ResponseEntity.ok(NotificacionDTO.fromModel(nueva));
        } catch (Exception e) {
            logger.error("Error al crear notificación: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> listarNotificaciones() {
        logger.info("GET /notificaciones - Listando notificaciones");
        List<Notificacion> lista = notificacionService.listar();
        
        List<NotificacionDTO> dtos = lista.stream()
                .map(NotificacionDTO::fromModel)
                .collect(Collectors.toList());
                
        logger.info("Total notificaciones listadas: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> obtenerNotificacion(@PathVariable Long id) {
        logger.info("GET /notificaciones/{} - Obteniendo notificación", id);
        try {
            Notificacion notificacion = notificacionService.obtenerPorId(id);
            logger.info("Notificación obtenida id={}", id);
            return ResponseEntity.ok(NotificacionDTO.fromModel(notificacion));
        } catch (Exception e) {
            logger.error("Error al obtener notificación id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificacionDTO> actualizarNotificacion(@PathVariable Long id, @Valid @RequestBody NotificacionDTO dto) {
        logger.info("PUT /notificaciones/{} - Actualizando notificación", id);
        try {
            Notificacion actualizada = notificacionService.actualizar(id, dto.toModel());
            logger.info("Notificación actualizada exitosamente id={}", id);
            return ResponseEntity.ok(NotificacionDTO.fromModel(actualizada));
        } catch (Exception e) {
            logger.error("Error al actualizar notificación id={}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarNotificacion(@PathVariable Long id) {
        logger.info("DELETE /notificaciones/{} - Eliminando notificación", id);
        try {
            notificacionService.eliminar(id);
            logger.info("Notificación eliminada exitosamente id={}", id);
            return ResponseEntity.ok("Notificación Eliminada Exitosamente");
        } catch (Exception e) {
            logger.error("Error al eliminar notificación id={}: {}", id, e.getMessage());
            throw e;
        }
    }
}
