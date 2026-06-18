package Nexus_Fashion.envio_service.controller;

import java.util.stream.Collectors;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// --- IMPORTACIONES DE HATEOAS CRÍTICAS ---
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
// ------------------------------------------

import Nexus_Fashion.envio_service.assemblers.EnvioModelAssembler;
import Nexus_Fashion.envio_service.dto.EnvioDTO;
import Nexus_Fashion.envio_service.model.Envio;
import Nexus_Fashion.envio_service.service.EnvioService;

@RestController
@RequestMapping("/envios")
public class EnvioController {

    private static final Logger logger = LoggerFactory.getLogger(EnvioController.class);
    private final EnvioService envioService;
    private final EnvioModelAssembler assembler; // Inyectamos el ensamblador

    // Constructor actualizado con ambas dependencias
    public EnvioController(EnvioService envioService, EnvioModelAssembler assembler) {
        this.envioService = envioService;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<?> crearEnvio(@RequestBody EnvioDTO envioDto) {
        logger.info("POST /envios - Solicitud de despacho recibida para la compra ID={}", envioDto.getIdCompra());
        try {
            Envio nuevoEnvio = envioService.guardar(envioDto.toModel());
            return ResponseEntity.status(HttpStatus.CREATED).body(EnvioDTO.fromModel(nuevoEnvio));
        } catch (RuntimeException e) {
            logger.warn("POST /envios - No se pudo procesar el envío: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // --- MODIFICADO CON HATEOAS: Listar todos ---
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<EnvioDTO>>> listarTodosLosEnvios() {
        logger.info("GET /envios - Solicitando lista completa de despachos");
        
        List<Envio> envios = envioService.obtenerTodos();
        List<EntityModel<EnvioDTO>> dtosConLinks = envios.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
                
        logger.info("GET /envios - Se encontraron {} registros de envíos", dtosConLinks.size());
        
        CollectionModel<EntityModel<EnvioDTO>> modeloFinal = CollectionModel.of(dtosConLinks,
                linkTo(methodOn(EnvioController.class).listarTodosLosEnvios()).withSelfRel());
                
        return ResponseEntity.ok(modeloFinal);
    }

    // --- MODIFICADO CON HATEOAS: Buscar por ID ---
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<EnvioDTO>> obtenerEnvioPorId(@PathVariable Long id) {
        logger.info("GET /envios/{} - Buscando registro de envío", id);
        
        Envio envio = envioService.buscarPorId(id);
        if (envio == null) {
            logger.warn("GET /envios/{} - No se encontró el envío solicitado", id);
            return ResponseEntity.notFound().build();
        }
        
        logger.info("GET /envios/{} - Registro de envío recuperado con éxito", id);
        return ResponseEntity.ok(assembler.toModel(envio));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnvioDTO> actualizarEnvio(@PathVariable Long id, @RequestBody EnvioDTO envioDto) {
        logger.info("PUT /envios/{} - Iniciando actualización", id);
        
        try {
            Envio actualizado = envioService.actualizar(id, envioDto.toModel());
            logger.info("PUT /envios/{} - Envío actualizado exitosamente", id);
            return ResponseEntity.ok(EnvioDTO.fromModel(actualizado));
        } catch (Exception e) {
            logger.warn("PUT /envios/{} - Error al actualizar: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEnvio(@PathVariable Long id) {
        logger.info("DELETE /envios/{} - Iniciando eliminación", id);
        
        try {
            envioService.eliminar(id);
            logger.info("DELETE /envios/{} - Envío eliminado exitosamente", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.warn("DELETE /envios/{} - Error al eliminar: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
