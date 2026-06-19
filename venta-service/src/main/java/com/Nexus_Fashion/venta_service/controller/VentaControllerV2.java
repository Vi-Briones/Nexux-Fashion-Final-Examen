package com.Nexus_Fashion.venta_service.controller;

import com.Nexus_Fashion.venta_service.assemblers.VentaModelAssembler;
import com.Nexus_Fashion.venta_service.dto.VentaDTO;
import com.Nexus_Fashion.venta_service.service.VentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/ventas")
public class VentaControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(VentaControllerV2.class);
    private final VentaService ventaService;
    private final VentaModelAssembler assembler;

    public VentaControllerV2(VentaService ventaService, VentaModelAssembler assembler) {
        this.ventaService = ventaService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<VentaDTO>>> listarVentas() {
        logger.info("GET /v2/ventas - Historial con HATEOAS");
        List<VentaDTO> ventas = ventaService.listar();
        
        List<EntityModel<VentaDTO>> dtosConLinks = ventas.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
                
        CollectionModel<EntityModel<VentaDTO>> modeloFinal = CollectionModel.of(dtosConLinks,
                linkTo(methodOn(VentaControllerV2.class).listarVentas()).withSelfRel());
        return ResponseEntity.ok(modeloFinal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<VentaDTO>> obtenerVenta(@PathVariable Long id) {
        logger.info("GET /v2/ventas/{} - Buscando comprobante HATEOAS", id);
        VentaDTO dto = ventaService.buscarPorId(id);
        if (dto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(assembler.toModel(dto));
    }
}