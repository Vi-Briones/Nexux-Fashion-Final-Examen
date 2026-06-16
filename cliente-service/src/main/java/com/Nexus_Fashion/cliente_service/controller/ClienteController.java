package com.Nexus_Fashion.cliente_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.Nexus_Fashion.cliente_service.assemblers.ClienteModelAssembler;
import com.Nexus_Fashion.cliente_service.dto.ClienteDTO;
import com.Nexus_Fashion.cliente_service.model.Cliente;
import com.Nexus_Fashion.cliente_service.service.ClienteService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;
    private final ClienteModelAssembler assembler; // Inyectamos el ensamblador corregido

    // Constructor único para inyección de dependencias de Spring
    public ClienteController(ClienteService clienteService, ClienteModelAssembler assembler) {
        this.clienteService = clienteService;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@RequestBody ClienteDTO clienteDto) {
        logger.info("POST /clientes - Intentando registrar un nuevo cliente");
        Cliente nuevoCliente = clienteService.guardar(clienteDto.toModel());
        logger.info("POST /clientes - Cliente registrado con éxito de forma interna");
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteDTO.fromModel(nuevoCliente));
    }
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ClienteDTO>>> listarClientes() {
        logger.info("GET /clientes - Solicitando la lista completa de clientes");
        List<Cliente> clientes = clienteService.listar();
        List<EntityModel<ClienteDTO>> dtosConLinks = clientes.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        logger.info("GET /clientes - Se retornaron {} clientes exitosamente", dtosConLinks.size());
        CollectionModel<EntityModel<ClienteDTO>> modeloFinal = CollectionModel.of(dtosConLinks,
                linkTo(methodOn(ClienteController.class).listarClientes()).withSelfRel());
                
        return ResponseEntity.ok(modeloFinal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClienteDTO>> obtenerCliente(@PathVariable Long id) {
        logger.info("GET /clientes/{} - Buscando cliente por ID", id);    
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            logger.warn("GET /clientes/{} - No se encontró el cliente", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("GET /clientes/{} - Cliente recuperado exitosamente", id);
        return ResponseEntity.ok(assembler.toModel(cliente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizarCliente(@PathVariable Long id, @RequestBody ClienteDTO clienteDto) {
        logger.info("PUT /clientes/{} - Iniciando actualización", id);
        try {
            Cliente actualizado = clienteService.actualizar(id, clienteDto.toModel());
            logger.info("PUT /clientes/{} - Cliente actualizado exitosamente", id);
            return ResponseEntity.ok(ClienteDTO.fromModel(actualizado));
        } catch (Exception e) {
            logger.warn("PUT /clientes/{} - Error al actualizar: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        logger.info("DELETE /clientes/{} - Iniciando eliminación", id);
        try {
            clienteService.eliminar(id);
            logger.info("DELETE /clientes/{} - Cliente eliminado exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.warn("DELETE /clientes/{} - Error al eliminar: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeCliente(@PathVariable Long id) {
        logger.info("GET /clientes/{}/exists - Petición de validación externa recibida", id);
        Boolean existe = clienteService.existePorId(id);
        
        if (Boolean.TRUE.equals(existe)) {
            logger.info("GET /clientes/{}/exists - Resultado: El cliente SÍ existe", id);
        } else {
            logger.warn("GET /clientes/{}/exists - Resultado: El cliente NO existe en la base de datos", id);
        }
        
        return ResponseEntity.ok(existe);
    }
}