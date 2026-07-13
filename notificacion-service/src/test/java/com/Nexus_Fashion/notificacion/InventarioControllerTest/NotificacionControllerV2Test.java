package com.Nexus_Fashion.notificacion.InventarioControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.Nexus_Fashion.notificacion_service.assemblers.NotificacionModelAssembler;
import com.Nexus_Fashion.notificacion_service.controller.NotificacionControllerV2;
import com.Nexus_Fashion.notificacion_service.exepcion.GlobalExceptionHandler;
import com.Nexus_Fashion.notificacion_service.exepcion.ResourceNotFoundException;
import com.Nexus_Fashion.notificacion_service.model.Notificacion;
import com.Nexus_Fashion.notificacion_service.service.NotificacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class NotificacionControllerV2Test {

    private MockMvc mockMvc;

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private NotificacionModelAssembler assembler;

    private Notificacion notificacionEjemplo;

    @BeforeEach
    void setUp() {
        NotificacionControllerV2 controller = new NotificacionControllerV2(notificacionService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        notificacionEjemplo = Notificacion.builder()
                .id(1L)
                .idUsuario(5L)
                .tipoEvento("DESPACHO_EN_CAMINO")
                .mensaje("Nexus Fashion: tu pedido está en camino")
                .leido(false)
                .fechaEnvio(LocalDateTime.of(2024, 6, 15, 10, 30))
                .build();
    }

    // ─────────────────────────── GET /notificaciones/v2 ────────────

    @Test
    void testListarNotificaciones_retornaListaHateoas() throws Exception {
        EntityModel<Notificacion> entityModel = EntityModel.of(notificacionEjemplo);
        when(notificacionService.listar()).thenReturn(Arrays.asList(notificacionEjemplo));
        when(assembler.toModel(notificacionEjemplo)).thenReturn(entityModel);

        mockMvc.perform(get("/notificaciones/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notificacionService, times(1)).listar();
        verify(assembler, times(1)).toModel(notificacionEjemplo);
    }

    @Test
    void testListarNotificaciones_listaVacia_retorna200() throws Exception {
        when(notificacionService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/notificaciones/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notificacionService, times(1)).listar();
        verify(assembler, never()).toModel(any());
    }

    // ─────────────────────────── GET /notificaciones/v2/{id} ───────

    @Test
    void testObtenerNotificacion_encontrado_retorna200() throws Exception {
        EntityModel<Notificacion> entityModel = EntityModel.of(notificacionEjemplo);
        when(notificacionService.obtenerPorId(1L)).thenReturn(notificacionEjemplo);
        when(assembler.toModel(notificacionEjemplo)).thenReturn(entityModel);

        mockMvc.perform(get("/notificaciones/v2/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notificacionService, times(1)).obtenerPorId(1L);
        verify(assembler, times(1)).toModel(notificacionEjemplo);
    }

    @Test
    void testObtenerNotificacion_noEncontrado_retorna404() throws Exception {
        when(notificacionService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Notificación no existe"));

        mockMvc.perform(get("/notificaciones/v2/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(notificacionService, times(1)).obtenerPorId(99L);
        verify(assembler, never()).toModel(any());
    }
}