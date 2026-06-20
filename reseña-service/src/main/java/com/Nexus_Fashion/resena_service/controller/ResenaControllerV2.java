package com.Nexus_Fashion.resena_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.Nexus_Fashion.resena_service.assemblers.ResenaModelAssembler;
import com.Nexus_Fashion.resena_service.dto.ResenaDTO;
import com.Nexus_Fashion.resena_service.service.ResenaService;

@RestController
@RequestMapping("/resenas/v2")
public class ResenaControllerV2 {

    private final ResenaService resenaService;
    private final ResenaModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(ResenaControllerV2.class);

    public ResenaControllerV2(ResenaService resenaService, ResenaModelAssembler assembler) {
        this.resenaService = resenaService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<ResenaDTO>> listarResenas() {
        logger.info("V2 GET /resenas - Listando reseñas"); //
        
        List<EntityModel<ResenaDTO>> resenas = resenaService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(resenas, 
                linkTo(methodOn(ResenaControllerV2.class).listarResenas()).withSelfRel()); //
    }

    @GetMapping("/{id}")
    public EntityModel<ResenaDTO> obtenerResena(@PathVariable Long id) {
        logger.info("V2 GET /resenas/{} - Obteniendo reseña", id); //
        
        ResenaDTO resenaDTO = resenaService.obtenerPorId(id);
        return assembler.toModel(resenaDTO); //
    }

}
