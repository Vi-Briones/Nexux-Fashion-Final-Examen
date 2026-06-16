package com.Nexus_Fashion.compra_service.controller;

import java.util.stream.Collectors;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.Nexus_Fashion.compra_service.assemblers.*;
import com.Nexus_Fashion.compra_service.dto.CompraDTO;
import com.Nexus_Fashion.compra_service.model.Compra;
import com.Nexus_Fashion.compra_service.service.CompraService;

@RestController
@RequestMapping("/compras")
public class CompraController {

    private static final Logger logger = LoggerFactory.getLogger(CompraController.class);

    private final CompraService compraService;
    private final CompraModelAssembler assembler;
    

    public CompraController(CompraService compraService, CompraModelAssembler assembler) {
        this.compraService = compraService;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<CompraDTO> crearCompra(@RequestBody CompraDTO compraDto) {

        logger.info("POST /compras - idCliente={}, idProducto={}",
                compraDto.getIdCliente(), compraDto.getIdProducto());

        Compra nuevaCompra = compraService.guardar(compraDto.toModel());

        logger.info("Compra creada exitosamente id={}", nuevaCompra.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(CompraDTO.fromModel(nuevaCompra));
    }

    @GetMapping
    public ResponseEntity<CollectionModel<CompraDTO>> listarCompras() {
        logger.info("GET /compras");

        List<Compra> compras = compraService.listar();

        logger.debug("Cantidad de compras obtenidas: {}", compras.size());

        List<EntityModel<CompraDTO>> dtosConLinks = compras.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        logger.info("GET /compras - Se retornaron {} compras exitosamente", dtosConLinks.size());
        CollectionModel<EntityModel<CompraDTO>> modeloFinal = CollectionModel.of(dtosConLinks,
                linkTo(methodOn(CompraController.class).listarCompras()).withSelfRel());
        return ResponseEntity.ok(modeloFinal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraDTO> obtenerCompra(@PathVariable Long id) {
        logger.info("GET /compras/{} - Buscando compra por ID", id);
        
        Compra compra = compraService.buscarPorId(id);
        if (compra == null) {
            logger.warn("GET /compras/{} - No se encontró la compra", id);
            return ResponseEntity.notFound().build();
        }
        
        logger.info("GET /compras/{} - Compra recuperada exitosamente", id);
        return ResponseEntity.ok(CompraDTO.fromModel(compra));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompraDTO> actualizarCompra(@PathVariable Long id, @RequestBody CompraDTO compraDto) {
        logger.info("PUT /compras/{} - Iniciando actualización", id);
        
        try {
            Compra actualizada = compraService.actualizar(id, compraDto.toModel());
            logger.info("PUT /compras/{} - Compra actualizada exitosamente", id);
            return ResponseEntity.ok(CompraDTO.fromModel(actualizada));
        } catch (Exception e) {
            logger.warn("PUT /compras/{} - Error al actualizar: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCompra(@PathVariable Long id) {
        logger.info("DELETE /compras/{} - Iniciando eliminación", id);
        
        try {
            compraService.eliminar(id);
            logger.info("DELETE /compras/{} - Compra eliminada exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.warn("DELETE /compras/{} - Error al eliminar: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<CompraDTO>> listarComprasPorCliente(@PathVariable Long idCliente) {
        logger.info("GET /compras/cliente/{} - Listando compras por cliente", idCliente);
        
        List<Compra> compras = compraService.listarPorCliente(idCliente);
        List<CompraDTO> dtos = compras.stream()
                .map(CompraDTO::fromModel)
                .collect(Collectors.toList());
        
        logger.info("GET /compras/cliente/{} - Se encontraron {} compras", idCliente, dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/cliente/{idCliente}/total")
    public ResponseEntity<Long> totalComprasPorCliente(@PathVariable Long idCliente) {
        logger.info("GET /compras/cliente/{}/total - Calculando total de compras", idCliente);
        
        long total = compraService.totalComprasPorCliente(idCliente);
        
        logger.info("GET /compras/cliente/{}/total - Total: {}", idCliente, total);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeCompra(@PathVariable Long id) {
        logger.info("GET /compras/{}/exists - Validación externa de compra solicitada", id);
        
        Boolean existe = compraService.existePorId(id);
        
        if (Boolean.TRUE.equals(existe)) {
            logger.info("GET /compras/{}/exists - Resultado: La compra existe", id);
        } else {
            logger.warn("GET /compras/{}/exists - Resultado: La compra NO existe", id);
        }
        
        return ResponseEntity.ok(existe);
    }
}
