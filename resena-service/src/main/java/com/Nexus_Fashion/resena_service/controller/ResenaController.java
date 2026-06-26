package com.Nexus_Fashion.resena_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Nexus_Fashion.resena_service.dto.ResenaDTO;
import com.Nexus_Fashion.resena_service.service.ResenaService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    // Inyección por constructor idéntica a tus otros servicios
    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    // 1. Endpoint para listar todas las reseñas
    @GetMapping
    public ResponseEntity<List<ResenaDTO>> listar() {
        return ResponseEntity.ok(resenaService.listar());
    }

    // 2. Endpoint para obtener una reseña específica por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ResenaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    // 3. Endpoint para guardar una nueva reseña
    @PostMapping
    public ResponseEntity<ResenaDTO> guardar(@Valid @RequestBody ResenaDTO resenaDTO) {
        ResenaDTO creada = resenaService.guardar(resenaDTO);
        return new ResponseEntity<>(creada, HttpStatus.CREATED); 
    }

    // 4. Endpoint para actualizar una reseña existente por su ID
    @PutMapping("/{id}")
    public ResponseEntity<ResenaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ResenaDTO resenaDTO) {
        ResenaDTO actualizada = resenaService.actualizar(id, resenaDTO);
        return ResponseEntity.ok(actualizada);
    }

    // 5. Endpoint para eliminar una reseña por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build(); // Devuelve un estado 204 No Content
    }
}
