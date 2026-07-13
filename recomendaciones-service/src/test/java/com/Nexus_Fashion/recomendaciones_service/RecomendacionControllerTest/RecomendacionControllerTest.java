package com.Nexus_Fashion.recomendaciones_service.RecomendacionControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.Nexus_Fashion.recomendaciones_service.controller.RecomendacionController;
import com.Nexus_Fashion.recomendaciones_service.dto.RecomendacionDTO;
import com.Nexus_Fashion.recomendaciones_service.exception.GlobalExceptionHandler;
import com.Nexus_Fashion.recomendaciones_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;
import com.Nexus_Fashion.recomendaciones_service.service.RecomendacionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class RecomendacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RecomendacionService recomendacionService;

    private ObjectMapper objectMapper;

    private Recomendacion recomendacionEjemplo;
    private RecomendacionDTO recomendacionDtoRequest;

    @BeforeEach
    void setUp() {
        RecomendacionController controller = new RecomendacionController(recomendacionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        recomendacionEjemplo = new Recomendacion(
                1L, 5L, 10L,
                "PRODUCTO_SIMILAR",
                "Te recomendamos este producto basado en tus compras anteriores",
                85.0
        );

        recomendacionDtoRequest = new RecomendacionDTO(
                null, 5L, 10L,
                "PRODUCTO_SIMILAR",
                "Te recomendamos este producto basado en tus compras anteriores",
                85.0
        );
    }

    // ─────────────────────────── POST ──────────────────────────────

    @Test
    void testCrearRecomendacion_retorna201() throws Exception {
        when(recomendacionService.guardar(any(Recomendacion.class))).thenReturn(recomendacionEjemplo);

        mockMvc.perform(post("/recomendaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recomendacionDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipoRecomendacion").value("PRODUCTO_SIMILAR"))
                .andExpect(jsonPath("$.idCliente").value(5L))
                .andExpect(jsonPath("$.puntajeAfinidad").value(85.0));
    }

    @Test
    void testCrearRecomendacion_servicioLanzaExcepcion_retorna400() throws Exception {
        when(recomendacionService.guardar(any(Recomendacion.class)))
                .thenThrow(new ResourceNotFoundException("Cliente no existe"));

        mockMvc.perform(post("/recomendaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recomendacionDtoRequest)))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────── GET /recomendaciones ──────────────

    @Test
    void testListarRecomendaciones_retornaLista() throws Exception {
        when(recomendacionService.listar()).thenReturn(Arrays.asList(recomendacionEjemplo));

        mockMvc.perform(get("/recomendaciones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].tipoRecomendacion").value("PRODUCTO_SIMILAR"))
                .andExpect(jsonPath("$[0].idCliente").value(5L));
    }

    @Test
    void testListarRecomendaciones_listaVacia_retorna200() throws Exception {
        when(recomendacionService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/recomendaciones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ─────────────────────────── GET /recomendaciones/{id} ─────────

    @Test
    void testObtenerRecomendacion_encontrado_retorna200() throws Exception {
        when(recomendacionService.buscarPorId(1L)).thenReturn(recomendacionEjemplo);

        mockMvc.perform(get("/recomendaciones/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.comentario")
                        .value("Te recomendamos este producto basado en tus compras anteriores"));
    }

    @Test
    void testObtenerRecomendacion_noEncontrado_retorna404() throws Exception {
        when(recomendacionService.buscarPorId(99L)).thenReturn(null);

        mockMvc.perform(get("/recomendaciones/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────── PUT /recomendaciones/{id} ─────────

    @Test
    void testActualizarRecomendacion_exitoso_retorna200() throws Exception {
        Recomendacion actualizada = new Recomendacion(
                1L, 5L, 10L,
                "TENDENCIA",
                "Producto en tendencia esta temporada",
                90.0
        );
        when(recomendacionService.actualizar(any(Long.class), any(Recomendacion.class)))
                .thenReturn(actualizada);

        RecomendacionDTO dtoActualizado = new RecomendacionDTO(
                null, 5L, 10L,
                "TENDENCIA",
                "Producto en tendencia esta temporada",
                90.0
        );

        mockMvc.perform(put("/recomendaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoRecomendacion").value("TENDENCIA"))
                .andExpect(jsonPath("$.puntajeAfinidad").value(90.0));
    }

    @Test
    void testActualizarRecomendacion_noExiste_retorna404() throws Exception {
        when(recomendacionService.actualizar(any(Long.class), any(Recomendacion.class)))
                .thenThrow(new ResourceNotFoundException("Recomendacion no encontrada"));

        mockMvc.perform(put("/recomendaciones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recomendacionDtoRequest)))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────── DELETE /recomendaciones/{id} ──────

    @Test
    void testEliminarRecomendacion_exitoso_retorna204() throws Exception {
        doNothing().when(recomendacionService).eliminar(1L);

        mockMvc.perform(delete("/recomendaciones/1"))
                .andExpect(status().isNoContent());

        verify(recomendacionService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminarRecomendacion_noExiste_retorna404() throws Exception {
        doThrow(new ResourceNotFoundException("Recomendacion no encontrada"))
                .when(recomendacionService).eliminar(99L);

        mockMvc.perform(delete("/recomendaciones/99"))
                .andExpect(status().isNotFound());
    }

    // ─────── GET /recomendaciones/cliente/{idCliente} ──────────────

    @Test
    void testListarPorCliente_retornaLista() throws Exception {
        when(recomendacionService.listarPorCliente(5L)).thenReturn(Arrays.asList(recomendacionEjemplo));

        mockMvc.perform(get("/recomendaciones/cliente/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCliente").value(5L))
                .andExpect(jsonPath("$[0].tipoRecomendacion").value("PRODUCTO_SIMILAR"));
    }

    @Test
    void testListarPorCliente_sinResultados_retornaListaVacia() throws Exception {
        when(recomendacionService.listarPorCliente(99L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/recomendaciones/cliente/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}