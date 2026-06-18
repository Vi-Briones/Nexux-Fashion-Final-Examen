package com.Nexus_Fashion.venta_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.Nexus_Fashion.venta_service.controller.VentaController;
import com.Nexus_Fashion.venta_service.dto.VentaDTO;

@Component
public class VentaModelAssembler implements RepresentationModelAssembler<VentaDTO, EntityModel<VentaDTO>> {

    @Override
    public EntityModel<VentaDTO> toModel(VentaDTO dto) {

        EntityModel<VentaDTO> model = EntityModel.of(dto);


        model.add(linkTo(methodOn(VentaController.class).obtenerVenta(dto.getIdVenta())).withSelfRel());
        
       
        model.add(linkTo(methodOn(VentaController.class).listarVentas()).withRel("todas-las-ventas"));
        
        return model;
    }
}