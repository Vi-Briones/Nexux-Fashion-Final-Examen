package com.Nexus_Fashion.inventario_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.*;

import com.Nexus_Fashion.inventario_service.assemblers.InventarioModelAssembler;
import com.Nexus_Fashion.inventario_service.model.Inventario;
import com.Nexus_Fashion.inventario_service.service.InventarioService;


@RestController
@RequestMapping("/inventarios/v2")
public class InventarioControllerV2 {

    private final InventarioService inventarioService;
    private final InventarioModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(InventarioControllerV2.class);

    public InventarioControllerV2(InventarioService inventarioService, InventarioModelAssembler assembler) {
        this.inventarioService = inventarioService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Inventario>> listarInventarios() {
        logger.info("V2 GET /inventarios - Listando inventarios con soporte HATEOAS");
        
        List<EntityModel<Inventario>> inventarios = inventarioService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
                
        return CollectionModel.of(inventarios, 
                linkTo(methodOn(InventarioControllerV2.class).listarInventarios()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Inventario> obtenerInventario(@PathVariable Long id) {
        logger.info("V2 GET /inventarios/{} - Obteniendo inventario específico con soporte HATEOAS", id);
        
        // El manejo de excepciones si no lo encuentra se delega al GlobalExceptionHandler
        return inventarioService.obtenerPorId(id) != null ? assembler.toModel(inventarioService.obtenerPorId(id)) : null;
    }
}
