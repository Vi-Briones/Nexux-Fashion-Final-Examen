package com.Nexus_Fashion.producto_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.Nexus_Fashion.producto_service.assemblers.ProductoModelAssembler;
import com.Nexus_Fashion.producto_service.dto.ProductoDTO;
import com.Nexus_Fashion.producto_service.model.Producto;
import com.Nexus_Fashion.producto_service.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/productos") 
public class ProductoControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(ProductoControllerV2.class);
    private final ProductoService productoService;
    private final ProductoModelAssembler assembler;

    public ProductoControllerV2(ProductoService productoService, ProductoModelAssembler assembler) {
        this.productoService = productoService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProductoDTO>>> listarProductos() {
        logger.info("GET /v2/productos - Catálogo completo de productos HATEOAS");
        List<Producto> productos = productoService.listar();
        
        List<EntityModel<ProductoDTO>> dtosConLinks = productos.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
                
        CollectionModel<EntityModel<ProductoDTO>> modeloFinal = CollectionModel.of(dtosConLinks,
                linkTo(methodOn(ProductoControllerV2.class).listarProductos()).withSelfRel());
        return ResponseEntity.ok(modeloFinal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoDTO>> obtenerProducto(@PathVariable Long id) {
        logger.info("GET /v2/productos/{} - Buscando por ID HATEOAS", id);
        Producto producto = productoService.buscarPorId(id);
        if (producto == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(assembler.toModel(producto));
    }
}