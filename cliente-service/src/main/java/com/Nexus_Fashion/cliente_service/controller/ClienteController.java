package com.Nexus_Fashion.cliente_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Nexus_Fashion.cliente_service.dto.ClienteDTO;
import com.Nexus_Fashion.cliente_service.model.Cliente;
import com.Nexus_Fashion.cliente_service.service.ClienteService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;

    // Constructor único para inyección de dependencias de Spring
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@RequestBody ClienteDTO clienteDto) {
        logger.info("POST /clientes - Intentando registrar un nuevo cliente");
        Cliente nuevoCliente = clienteService.guardar(clienteDto.toModel());
        logger.info("POST /clientes - Cliente registrado con éxito de forma interna");
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteDTO.fromModel(nuevoCliente));
    }
    
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        logger.info("GET /clientes - Solicitando la lista completa de clientes");
        List<Cliente> clientes = clienteService.listar();
        List<ClienteDTO> dtos = clientes.stream()
                .map(ClienteDTO::fromModel)
                .collect(Collectors.toList());
        logger.info("GET /clientes - Se retornaron {} clientes exitosamente", dtos.size());
                
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerCliente(@PathVariable Long id) {
        logger.info("GET /clientes/{} - Buscando cliente por ID", id);    
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente == null) {
            logger.warn("GET /clientes/{} - No se encontró el cliente", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("GET /clientes/{} - Cliente recuperado exitosamente", id);
        return ResponseEntity.ok(ClienteDTO.fromModel(cliente));
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
}