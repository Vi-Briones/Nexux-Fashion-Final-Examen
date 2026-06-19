package com.Nexus_Fashion.producto_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Nexus_Fashion.producto_service.dto.ProductoDTO;
import com.Nexus_Fashion.producto_service.model.Producto;
import com.Nexus_Fashion.producto_service.service.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos") 
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO productoDto) {
        logger.info("POST /productos - Intentando registrar un nuevo producto en el catálogo");
        
        Producto nuevo = productoService.guardar(productoDto.toModel());
        
        logger.info("POST /productos - Producto creado con éxito internamente");
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductoDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        logger.info("GET /productos - Solicitando catálogo completo de productos");
        
        List<Producto> productos = productoService.listar();
        List<ProductoDTO> dtos = productos.stream()
                .map(ProductoDTO::fromModel)
                .collect(Collectors.toList());
                
        logger.info("GET /productos - Catálogo enviado. Total productos: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerProducto(@PathVariable Long id) {
        logger.info("GET /productos/{} - Buscando producto por ID", id);
        
        Producto producto = productoService.buscarPorId(id);
        if (producto == null) {
            logger.warn("GET /productos/{} - No se encontró el producto", id);
            return ResponseEntity.notFound().build();
        }
        
        logger.info("GET /productos/{} - Producto recuperado exitosamente", id);
        return ResponseEntity.ok(ProductoDTO.fromModel(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDTO productoDto) {
        logger.info("PUT /productos/{} - Iniciando actualización", id);
        
        try {
            Producto actualizado = productoService.actualizar(id, productoDto.toModel());
            logger.info("PUT /productos/{} - Producto actualizado exitosamente", id);
            return ResponseEntity.ok(ProductoDTO.fromModel(actualizado));
        } catch (Exception e) {
            logger.warn("PUT /productos/{} - Error al actualizar: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        logger.info("DELETE /productos/{} - Iniciando eliminación", id);
        
        try {
            productoService.eliminar(id);
            logger.info("DELETE /productos/{} - Producto eliminado exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.warn("DELETE /productos/{} - Error al eliminar: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}