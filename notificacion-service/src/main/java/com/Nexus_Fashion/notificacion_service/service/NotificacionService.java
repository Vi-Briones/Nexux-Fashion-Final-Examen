package com.Nexus_Fashion.notificacion_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Nexus_Fashion.notificacion_service.model.Notificacion;
import com.Nexus_Fashion.notificacion_service.repository.NotificacionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public Notificacion guardar(Notificacion notificacion) {
        logger.info("Iniciando guardar notificación con idUsuario={}, tipoEvento={}, mensaje={}", 
            notificacion.getIdUsuario(), notificacion.getTipoEvento(), notificacion.getMensaje());
        try {
            if (notificacion.getIdUsuario() == null || notificacion.getIdUsuario() <= 0) {
                throw new IllegalArgumentException("idUsuario requerido y debe ser mayor a 0");
            }
            if (notificacion.getTipoEvento() == null || notificacion.getTipoEvento().isBlank()) {
                throw new IllegalArgumentException("tipoEvento requerido");
            }
            if (notificacion.getMensaje() == null || notificacion.getMensaje().isBlank()) {
                throw new IllegalArgumentException("mensaje requerido");
            }
            if (notificacion.getFechaEnvio() == null) {
                notificacion.setFechaEnvio(LocalDateTime.now());
            }

            Notificacion notificacionGuardada = notificacionRepository.save(notificacion);
            logger.info("Notificación guardada exitosamente con id={}", notificacionGuardada.getId());
            return notificacionGuardada;
        } catch (Exception e) {
            logger.error("Error al guardar notificación: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Notificacion> listar() {
        logger.info("Listando todas las notificaciones");
        List<Notificacion> notificaciones = notificacionRepository.findAll();
        logger.info("Total notificaciones encontradas: {}", notificaciones.size());
        return notificaciones;
    }

    public Notificacion obtenerPorId(Long id) {
        logger.info("Buscando notificación por id={}", id);
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Notificación no encontrada id={}", id);
                    return new RuntimeException("Notificación no existe"); 
                });
        logger.info("Notificación encontrada id={}", id);
        return notificacion;
    }

    public Notificacion actualizar(Long id, Notificacion notificacion) {
        logger.info("Iniciando actualizar notificación id={}", id);
        try {
            Notificacion existente = notificacionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Notificación no existe"));

            if (notificacion.getIdUsuario() == null || notificacion.getIdUsuario() <= 0) {
                throw new IllegalArgumentException("idUsuario requerido");
            }
            if (notificacion.getTipoEvento() == null || notificacion.getTipoEvento().isBlank()) {
                throw new IllegalArgumentException("tipoEvento requerido");
            }

            existente.setIdUsuario(notificacion.getIdUsuario());
            existente.setTipoEvento(notificacion.getTipoEvento());
            existente.setMensaje(notificacion.getMensaje());
            existente.setLeido(notificacion.getLeido());
            
            if (notificacion.getFechaEnvio() != null) {
                existente.setFechaEnvio(notificacion.getFechaEnvio());
            }

            Notificacion actualizada = notificacionRepository.save(existente);
            logger.info("Notificación actualizada exitosamente id={}", actualizada.getId());
            return actualizada;
        } catch (Exception e) {
            logger.error("Error al actualizar notificación id={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void eliminar(Long id) {
        logger.info("Iniciando eliminación de notificación id={}", id);
        try {
            if (!notificacionRepository.existsById(id)) {
                logger.warn("Notificación no existe para eliminar id={}", id);
                throw new RuntimeException("Notificación no existe");
            }
            notificacionRepository.deleteById(id);
            logger.info("Notificación eliminada exitosamente id={}", id);
        } catch (Exception e) {
            logger.error("Error al eliminar notificación id={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
