package com.Nexus_Fashion.cliente_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.Nexus_Fashion.cliente_service.assemblers.ClienteModelAssembler;
import com.Nexus_Fashion.cliente_service.dto.ClienteDTO;
import com.Nexus_Fashion.cliente_service.model.Cliente;
import com.Nexus_Fashion.cliente_service.service.ClienteService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/clientes")
public class ClienteControllerV2 {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteControllerV2.class);
    private final ClienteService clienteService;
    private final ClienteModelAssembler assembler;

    public ClienteControllerV2(ClienteService clienteService, ClienteModelAssembler assembler) {
        this.clienteService = clienteService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ClienteDTO>>> listarClientes() {
        logger.info("GET /v2/clientes - Solicitando la lista completa de clientes (HATEOAS)");
        List<Cliente> clientes = clienteService.listar();
        
        List<EntityModel<ClienteDTO>> dtosConLinks = clientes.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
                
        CollectionModel<EntityModel<ClienteDTO>> modeloFinal = CollectionModel.of(dtosConLinks,
                linkTo(methodOn(ClienteControllerV2.class).listarClientes()).withSelfRel());
                
        return ResponseEntity.ok(modeloFinal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClienteDTO>> obtenerCliente(@PathVariable Long id) {
        logger.info("GET /v2/clientes/{} - Buscando cliente por ID (HATEOAS)", id);    
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assembler.toModel(cliente));
    }
}