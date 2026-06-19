package com.Nexus_Fashion.compra_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.Nexus_Fashion.compra_service.controller.CompraController;
import com.Nexus_Fashion.compra_service.dto.CompraDTO;
import com.Nexus_Fashion.compra_service.model.Compra;

@Component
public class CompraModelAssembler implements RepresentationModelAssembler<Compra, EntityModel<CompraDTO>> {

    @Override
    public EntityModel<CompraDTO> toModel(Compra compra) {
        
        CompraDTO dto = CompraDTO.fromModel(compra);
        EntityModel<CompraDTO> model = EntityModel.of(dto);

        model.add(linkTo(methodOn(CompraController.class).obtenerCompra(compra.getId())).withSelfRel());
        model.add(linkTo(methodOn(CompraController.class).listarCompras()).withRel("todas-las-compras"));
        
        return model;}
    }