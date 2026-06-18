package com.Nexus_Fashion.producto_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.Nexus_Fashion.producto_service.controller.ProductoController;
import com.Nexus_Fashion.producto_service.dto.ProductoDTO;
import com.Nexus_Fashion.producto_service.model.Producto;

@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<Producto, EntityModel<ProductoDTO>> {

    @Override
    public EntityModel<ProductoDTO> toModel(Producto producto) {
        
        ProductoDTO dto = ProductoDTO.fromModel(producto);
        EntityModel<ProductoDTO> model = EntityModel.of(dto);

        model.add(linkTo(methodOn(ProductoController.class).obtenerProducto(dto.getIdProducto())).withSelfRel());
        model.add(linkTo(methodOn(ProductoController.class).listarProductos()).withRel("todos-los-productos"));
        
        return model;
    }
}
