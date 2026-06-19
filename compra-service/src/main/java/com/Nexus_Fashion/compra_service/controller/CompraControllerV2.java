package com.Nexus_Fashion.compra_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.Nexus_Fashion.compra_service.assemblers.CompraModelAssembler;
import com.Nexus_Fashion.compra_service.dto.CompraDTO;
import com.Nexus_Fashion.compra_service.model.Compra;
import com.Nexus_Fashion.compra_service.service.CompraService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/compras")
public class CompraControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(CompraControllerV2.class);
    private final CompraService compraService;
    private final CompraModelAssembler assembler;

    public CompraControllerV2(CompraService compraService, CompraModelAssembler assembler) {
        this.compraService = compraService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CompraDTO>>> listarCompras() {
        logger.info("GET /v2/compras - Catálogo completo con HATEOAS");
        List<Compra> compras = compraService.listar();

        List<EntityModel<CompraDTO>> dtosConLinks = compras.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<CompraDTO>> modeloFinal = CollectionModel.of(dtosConLinks,
                linkTo(methodOn(CompraControllerV2.class).listarCompras()).withSelfRel());
        return ResponseEntity.ok(modeloFinal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CompraDTO>> obtenerCompra(@PathVariable Long id) {
        logger.info("GET /v2/compras/{} - Buscando compra con HATEOAS", id);
        Compra compra = compraService.buscarPorId(id);
        if (compra == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(assembler.toModel(compra));
    }

}