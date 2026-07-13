package com.Nexus_Fashion.notificacion.InventarioControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.Nexus_Fashion.notificacion_service.controller.NotificacionController;
import com.Nexus_Fashion.notificacion_service.dto.NotificacionDTO;
import com.Nexus_Fashion.notificacion_service.exepcion.GlobalExceptionHandler;
import com.Nexus_Fashion.notificacion_service.exepcion.ResourceNotFoundException;
import com.Nexus_Fashion.notificacion_service.model.Notificacion;
import com.Nexus_Fashion.notificacion_service.service.NotificacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class NotificacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificacionService notificacionService;

    private ObjectMapper objectMapper;

    private Notificacion notificacionEjemplo;
    private NotificacionDTO notificacionDtoRequest;

    @BeforeEach
    void setUp() {
        NotificacionController controller = new NotificacionController(notificacionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        notificacionEjemplo = Notificacion.builder()
                .id(1L)
                .idUsuario(5L)
                .tipoEvento("DESPACHO_EN_CAMINO")
                .mensaje("Nexus Fashion: tu pedido está en camino")
                .leido(false)
                .fechaEnvio(LocalDateTime.of(2024, 6, 15, 10, 30))
                .build();

        notificacionDtoRequest = NotificacionDTO.builder()
                .idUsuario(5L)
                .tipoEvento("DESPACHO_EN_CAMINO")
                .mensaje("Nexus Fashion: tu pedido está en camino")
                .leido(false)
                .fechaEnvio(LocalDateTime.of(2024, 6, 15, 10, 30))
                .build();
    }

    // ─────────────────────────── POST ──────────────────────────────

    @Test
    void testCrearNotificacion_retorna200() throws Exception {
        when(notificacionService.guardar(any(Notificacion.class))).thenReturn(notificacionEjemplo);

        mockMvc.perform(post("/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tipoEvento").value("DESPACHO_EN_CAMINO"))
                .andExpect(jsonPath("$.idUsuario").value(5L))
                .andExpect(jsonPath("$.leido").value(false));
    }

    @Test
    void testCrearNotificacion_servicioLanzaExcepcion_retorna500() throws Exception {
        when(notificacionService.guardar(any(Notificacion.class)))
                .thenThrow(new IllegalArgumentException("idUsuario requerido y debe ser mayor a 0"));

        mockMvc.perform(post("/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionDtoRequest)))
                .andExpect(status().isInternalServerError());
    }

    // ─────────────────────────── GET /notificaciones ───────────────

    @Test
    void testListarNotificaciones_retornaLista() throws Exception {
        when(notificacionService.listar()).thenReturn(Arrays.asList(notificacionEjemplo));

        mockMvc.perform(get("/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].tipoEvento").value("DESPACHO_EN_CAMINO"))
                .andExpect(jsonPath("$[0].idUsuario").value(5L));
    }

    @Test
    void testListarNotificaciones_listaVacia_retorna200() throws Exception {
        when(notificacionService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ─────────────────────────── GET /notificaciones/{id} ──────────

    @Test
    void testObtenerNotificacion_encontrado_retorna200() throws Exception {
        when(notificacionService.obtenerPorId(1L)).thenReturn(notificacionEjemplo);

        mockMvc.perform(get("/notificaciones/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.mensaje").value("Nexus Fashion: tu pedido está en camino"));
    }

    @Test
    void testObtenerNotificacion_noEncontrado_retorna404() throws Exception {
        when(notificacionService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Notificación no existe"));

        mockMvc.perform(get("/notificaciones/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────── PUT /notificaciones/{id} ──────────

    @Test
    void testActualizarNotificacion_exitoso_retorna200() throws Exception {
        Notificacion actualizada = Notificacion.builder()
                .id(1L)
                .idUsuario(5L)
                .tipoEvento("PEDIDO_ENTREGADO")
                .mensaje("Tu pedido fue entregado exitosamente")
                .leido(true)
                .fechaEnvio(LocalDateTime.of(2024, 6, 15, 10, 30))
                .build();

        when(notificacionService.actualizar(any(Long.class), any(Notificacion.class)))
                .thenReturn(actualizada);

        NotificacionDTO dtoActualizado = NotificacionDTO.builder()
                .idUsuario(5L)
                .tipoEvento("PEDIDO_ENTREGADO")
                .mensaje("Tu pedido fue entregado exitosamente")
                .leido(true)
                .fechaEnvio(LocalDateTime.of(2024, 6, 15, 10, 30))
                .build();

        mockMvc.perform(put("/notificaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipoEvento").value("PEDIDO_ENTREGADO"))
                .andExpect(jsonPath("$.leido").value(true));
    }

    @Test
    void testActualizarNotificacion_noExiste_retorna404() throws Exception {
        when(notificacionService.actualizar(any(Long.class), any(Notificacion.class)))
                .thenThrow(new ResourceNotFoundException("Notificación no existe"));

        mockMvc.perform(put("/notificaciones/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(notificacionDtoRequest)))
                .andExpect(status().isNotFound());
    }

    // ─────────────────────────── DELETE /notificaciones/{id} ───────

    @Test
    void testEliminarNotificacion_exitoso_retorna200() throws Exception {
        doNothing().when(notificacionService).eliminar(1L);

        mockMvc.perform(delete("/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notificación Eliminada Exitosamente"));

        verify(notificacionService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminarNotificacion_noExiste_retorna404() throws Exception {
        doThrow(new ResourceNotFoundException("Notificación no existe"))
                .when(notificacionService).eliminar(99L);

        mockMvc.perform(delete("/notificaciones/99"))
                .andExpect(status().isNotFound());
    }
}