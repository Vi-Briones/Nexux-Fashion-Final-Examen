package com.Nexus_Fashion.cliente_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.Nexus_Fashion.cliente_service.controller.ClienteController;
import com.Nexus_Fashion.cliente_service.dto.ClienteDTO;
import com.Nexus_Fashion.cliente_service.model.Cliente;

@Component
public class ClienteModelAssembler implements RepresentationModelAssembler<Cliente, EntityModel<ClienteDTO>> {

    @Override
    public EntityModel<ClienteDTO> toModel(Cliente cliente) {

        ClienteDTO dto = ClienteDTO.fromModel(cliente);
        EntityModel<ClienteDTO> model = EntityModel.of(dto);

        model.add(linkTo(methodOn(ClienteController.class).obtenerCliente(dto.getIdCliente())).withSelfRel());
        model.add(linkTo(methodOn(ClienteController.class).listarClientes()).withRel("todos-los-clientes"));
        return model;
    }
}