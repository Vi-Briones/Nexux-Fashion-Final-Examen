package com.Nexus_Fashion.notificacion_service.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.Nexus_Fashion.notificacion_service.controller.NotificacionControllerV2;
import com.Nexus_Fashion.notificacion_service.model.Notificacion;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class NotificacionModelAssembler implements  RepresentationModelAssembler<Notificacion, EntityModel<Notificacion>>{

    @Override
    public EntityModel<Notificacion> toModel(Notificacion notificacion) {
        return EntityModel.of(notificacion,
            linkTo(methodOn(NotificacionControllerV2.class).obtenerNotificacion(notificacion.getId())).withSelfRel(),
            linkTo(methodOn(NotificacionControllerV2.class).listarNotificaciones()).withRel("notificaciones")
        );
    }
}
