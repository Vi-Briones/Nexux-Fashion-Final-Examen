package com.Nexus_Fashion.soporte_service.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.Nexus_Fashion.soporte_service.controller.SoporteControllerV2;
import com.Nexus_Fashion.soporte_service.model.Soporte;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class SoporteModelAssembler implements RepresentationModelAssembler<Soporte, EntityModel<Soporte>>{

    @Override
    public EntityModel<Soporte> toModel(Soporte soporte) {
        return EntityModel.of(soporte,
            linkTo(methodOn(SoporteControllerV2.class).obtenerSoporte(soporte.getId())).withSelfRel(),
            linkTo(methodOn(SoporteControllerV2.class).listarSoportes()).withRel("soportes")
        );
    }
}
