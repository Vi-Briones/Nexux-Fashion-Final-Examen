package com.Nexus_Fashion.recomendaciones_service.RecomendacionControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.Nexus_Fashion.recomendaciones_service.assemblers.RecomendacionModelAssembler;
import com.Nexus_Fashion.recomendaciones_service.controller.RecomendacionControllerV2;
import com.Nexus_Fashion.recomendaciones_service.dto.RecomendacionDTO;
import com.Nexus_Fashion.recomendaciones_service.exception.GlobalExceptionHandler;
import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;
import com.Nexus_Fashion.recomendaciones_service.service.RecomendacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class RecomendacionControllerV2Test {

    private MockMvc mockMvc;

    @Mock
    private RecomendacionService recomendacionService;

    @Mock
    private RecomendacionModelAssembler assembler;

    private Recomendacion recomendacionEjemplo;
    private RecomendacionDTO recomendacionDtoEjemplo;

    @BeforeEach
    void setUp() {
        RecomendacionControllerV2 controller = new RecomendacionControllerV2(recomendacionService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        recomendacionEjemplo = new Recomendacion(
                1L, 5L, 10L,
                "PRODUCTO_SIMILAR",
                "Te recomendamos este producto basado en tus compras anteriores",
                85.0
        );

        recomendacionDtoEjemplo = new RecomendacionDTO(
                1L, 5L, 10L,
                "PRODUCTO_SIMILAR",
                "Te recomendamos este producto basado en tus compras anteriores",
                85.0
        );
    }

    // ─────────────────────────── GET /v2/recomendaciones ───────────

    @Test
    void testListarRecomendaciones_retornaListaHateoas() throws Exception {
        EntityModel<RecomendacionDTO> entityModel = EntityModel.of(recomendacionDtoEjemplo);
        when(recomendacionService.listar()).thenReturn(Arrays.asList(recomendacionEjemplo));
        when(assembler.toModel(recomendacionEjemplo)).thenReturn(entityModel);

        mockMvc.perform(get("/v2/recomendaciones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(recomendacionService, times(1)).listar();
        verify(assembler, times(1)).toModel(recomendacionEjemplo);
    }

    @Test
    void testListarRecomendaciones_listaVacia_retorna200() throws Exception {
        when(recomendacionService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v2/recomendaciones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(recomendacionService, times(1)).listar();
        verify(assembler, never()).toModel(any());
    }

    // ─────────────────────────── GET /v2/recomendaciones/{id} ──────

    @Test
    void testObtenerRecomendacion_encontrado_retorna200() throws Exception {
        EntityModel<RecomendacionDTO> entityModel = EntityModel.of(recomendacionDtoEjemplo);
        when(recomendacionService.buscarPorId(1L)).thenReturn(recomendacionEjemplo);
        when(assembler.toModel(recomendacionEjemplo)).thenReturn(entityModel);

        mockMvc.perform(get("/v2/recomendaciones/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(recomendacionService, times(1)).buscarPorId(1L);
        verify(assembler, times(1)).toModel(recomendacionEjemplo);
    }

    @Test
    void testObtenerRecomendacion_noEncontrado_retorna404() throws Exception {
        when(recomendacionService.buscarPorId(99L)).thenReturn(null);

        mockMvc.perform(get("/v2/recomendaciones/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(recomendacionService, times(1)).buscarPorId(99L);
        verify(assembler, never()).toModel(any());
    }
}