package com.Nexus_Fashion.recomendaciones_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.Nexus_Fashion.recomendaciones_service.controller.RecomendacionControllerV2;
import com.Nexus_Fashion.recomendaciones_service.dto.RecomendacionDTO;
import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;

@Component
public class RecomendacionModelAssembler implements RepresentationModelAssembler<Recomendacion, EntityModel<RecomendacionDTO>> {

    @Override
    public EntityModel<RecomendacionDTO> toModel(Recomendacion recomendacion) {
        RecomendacionDTO dto = RecomendacionDTO.fromModel(recomendacion);
        return EntityModel.of(dto,
                linkTo(methodOn(RecomendacionControllerV2.class).obtenerRecomendacion(recomendacion.getId())).withSelfRel(),
                linkTo(methodOn(RecomendacionControllerV2.class).listarRecomendaciones()).withRel("recomendaciones"));
    }
}
