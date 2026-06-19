package Nexus_Fashion.envio_service.controller;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import Nexus_Fashion.envio_service.assemblers.EnvioModelAssembler;
import Nexus_Fashion.envio_service.dto.EnvioDTO;
import Nexus_Fashion.envio_service.model.Envio;
import Nexus_Fashion.envio_service.service.EnvioService;

@RestController
@RequestMapping("/v2/envios")
public class EnvioControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(EnvioControllerV2.class);
    private final EnvioService envioService;
    private final EnvioModelAssembler assembler;

    public EnvioControllerV2(EnvioService envioService, EnvioModelAssembler assembler) {
        this.envioService = envioService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<EnvioDTO>>> listarTodosLosEnvios() {
        logger.info("GET /v2/envios - Despachos con HATEOAS");
        List<Envio> envios = envioService.obtenerTodos();
        
        List<EntityModel<EnvioDTO>> dtosConLinks = envios.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
                
        CollectionModel<EntityModel<EnvioDTO>> modeloFinal = CollectionModel.of(dtosConLinks,
                linkTo(methodOn(EnvioControllerV2.class).listarTodosLosEnvios()).withSelfRel());
        return ResponseEntity.ok(modeloFinal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<EnvioDTO>> obtenerEnvioPorId(@PathVariable Long id) {
        logger.info("GET /v2/envios/{} - Buscando despacho HATEOAS", id);
        Envio envio = envioService.buscarPorId(id);
        if (envio == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(assembler.toModel(envio));
    }
}
