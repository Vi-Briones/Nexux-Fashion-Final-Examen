package com.Nexus_Fashion.resena_service.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.Nexus_Fashion.resena_service.controller.ResenaControllerV2;
import com.Nexus_Fashion.resena_service.dto.ResenaDTO;

@Component
public class ResenaModelAssembler implements RepresentationModelAssembler<ResenaDTO, EntityModel<ResenaDTO>>{

    @Override
    public EntityModel<ResenaDTO> toModel(ResenaDTO resenaDTO) {
        return EntityModel.of(resenaDTO,
                linkTo(methodOn(ResenaControllerV2.class).obtenerResena(resenaDTO.getId())).withSelfRel(),
                linkTo(methodOn(ResenaControllerV2.class).listarResenas()).withRel("resenas"));
    }
}
