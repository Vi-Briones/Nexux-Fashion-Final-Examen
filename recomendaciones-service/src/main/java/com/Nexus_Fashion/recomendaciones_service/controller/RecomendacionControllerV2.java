package com.Nexus_Fashion.recomendaciones_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.Nexus_Fashion.recomendaciones_service.assemblers.RecomendacionModelAssembler;
import com.Nexus_Fashion.recomendaciones_service.dto.RecomendacionDTO;
import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;
import com.Nexus_Fashion.recomendaciones_service.service.RecomendacionService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/recomendaciones")
public class RecomendacionControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(RecomendacionControllerV2.class);
    private final RecomendacionService recomendacionService;
    private final RecomendacionModelAssembler assembler;

    public RecomendacionControllerV2(RecomendacionService recomendacionService, RecomendacionModelAssembler assembler) {
        this.recomendacionService = recomendacionService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<RecomendacionDTO>>> listarRecomendaciones() {
        logger.info("GET /v2/recomendaciones - Listando recomendaciones con HATEOAS");
        List<Recomendacion> recomendaciones = recomendacionService.listar();

        List<EntityModel<RecomendacionDTO>> dtosConLinks = recomendaciones.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<RecomendacionDTO>> modeloFinal = CollectionModel.of(dtosConLinks,
                linkTo(methodOn(RecomendacionControllerV2.class).listarRecomendaciones()).withSelfRel());
        return ResponseEntity.ok(modeloFinal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<RecomendacionDTO>> obtenerRecomendacion(@PathVariable Long id) {
        logger.info("GET /v2/recomendaciones/{} - Buscando recomendación con HATEOAS", id);
        Recomendacion recomendacion = recomendacionService.buscarPorId(id);
        if (recomendacion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assembler.toModel(recomendacion));
    }
}
