package com.Nexus_Fashion.notificacion_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import com.Nexus_Fashion.notificacion_service.assemblers.NotificacionModelAssembler;
import com.Nexus_Fashion.notificacion_service.model.Notificacion;
import com.Nexus_Fashion.notificacion_service.service.NotificacionService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("notificaciones/v2")
public class NotificacionControllerV2 {

    private final NotificacionService notificacionService;
    private final NotificacionModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(NotificacionControllerV2.class);

    public NotificacionControllerV2(NotificacionService notificacionService, NotificacionModelAssembler assembler) {
        this.notificacionService = notificacionService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Notificacion>> listarNotificaciones() {
        logger.info("V2 GET /notificaciones - Listando notificaciones");
        
        List<EntityModel<Notificacion>> notificaciones = notificacionService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
                
        return CollectionModel.of(notificaciones,
                linkTo(methodOn(NotificacionControllerV2.class).listarNotificaciones()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Notificacion> obtenerNotificacion(@PathVariable Long id) {
        logger.info("V2 GET /notificaciones/{} - Obteniendo notificación", id);
        Notificacion notificacion = notificacionService.obtenerPorId(id);
        return assembler.toModel(notificacion);
    }
}
