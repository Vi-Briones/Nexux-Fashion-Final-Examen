package com.Nexus_Fashion.soporte_servicee.SoporteControllerTest;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.Nexus_Fashion.soporte_service.controller.SoporteController;
import com.Nexus_Fashion.soporte_service.dto.SoporteDTO;
import com.Nexus_Fashion.soporte_service.exepcion.GlobalExceptionHandler;
import com.Nexus_Fashion.soporte_service.model.Soporte;
import com.Nexus_Fashion.soporte_service.service.SoporteService;

@ExtendWith(MockitoExtension.class)
public class SoporteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SoporteService soporteService;

    private ObjectMapper objectMapper;

    private Soporte soporteEjemplo;
    private SoporteDTO dtoRequest;

    @BeforeEach
    void setUp() {
        SoporteController controller = new SoporteController(soporteService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        soporteEjemplo = new Soporte(1L, 10L, "Problema con el login", "No puedo ingresar a mi cuenta",
                "PENDIENTE", "ALTA", LocalDateTime.now(), null);

        dtoRequest = new SoporteDTO(null, 10L, "Problema con el login", "No puedo ingresar a mi cuenta",
                "PENDIENTE", "ALTA", null, null);
    }

    // ---------- crearSoporte ----------

    @Test
    void testCrearSoporte_retorna200() throws Exception {
        when(soporteService.guardar(any(Soporte.class))).thenReturn(soporteEjemplo);

        mockMvc.perform(post("/soportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.asunto").value("Problema con el login"));
    }

    @Test
    void testCrearSoporte_servicioLanzaExcepcion_retorna500() throws Exception {
        when(soporteService.guardar(any(Soporte.class)))
                .thenThrow(new IllegalArgumentException("idCliente (Cliente) requerido y debe ser mayor a 0"));

        mockMvc.perform(post("/soportes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRequest)))
                .andExpect(status().isInternalServerError());
    }

    // ---------- listarSoportes ----------

    @Test
    void testListarSoportes_retornaLista() throws Exception {
        when(soporteService.listar()).thenReturn(Arrays.asList(soporteEjemplo));

        mockMvc.perform(get("/soportes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testListarSoportes_listaVacia_retorna200() throws Exception {
        when(soporteService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/soportes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ---------- obtenerSoportes ----------

    @Test
    void testObtenerSoportes_encontrado_retorna200() throws Exception {
        when(soporteService.obtenerPorId(1L)).thenReturn(soporteEjemplo);

        mockMvc.perform(get("/soportes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testObtenerSoportes_noEncontrado_retorna500() throws Exception {
        when(soporteService.obtenerPorId(99L))
                .thenThrow(new RuntimeException("Ticket de soporte no existe"));

        mockMvc.perform(get("/soportes/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    // ---------- actualizarSoporte ----------

    @Test
    void testActualizarSoporte_exitoso_retorna200() throws Exception {
        Soporte actualizado = new Soporte(1L, 10L, "Asunto actualizado", "Descripcion actualizada",
                "EN_PROCESO", "MEDIA", LocalDateTime.now(), LocalDateTime.now());

        when(soporteService.actualizar(eq(1L), any(Soporte.class))).thenReturn(actualizado);

        SoporteDTO dtoActualizado = new SoporteDTO(null, 10L, "Asunto actualizado", "Descripcion actualizada",
                "EN_PROCESO", "MEDIA", null, null);

        mockMvc.perform(put("/soportes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    @Test
    void testActualizarSoporte_noExiste_retorna500() throws Exception {
        when(soporteService.actualizar(eq(99L), any(Soporte.class)))
                .thenThrow(new RuntimeException("Ticket de soporte no existe"));

        mockMvc.perform(put("/soportes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoRequest)))
                .andExpect(status().isInternalServerError());
    }

    // ---------- eliminarSoporte ----------

    @Test
    void testEliminarSoporte_exitoso_retorna200() throws Exception {
        doNothing().when(soporteService).eliminar(1L);

        mockMvc.perform(delete("/soportes/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ticket de soporte Eliminado Exitosamente"));

        verify(soporteService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminarSoporte_noExiste_retorna500() throws Exception {
        doThrow(new RuntimeException("Ticket de soporte no existe"))
                .when(soporteService).eliminar(99L);

        mockMvc.perform(delete("/soportes/99"))
                .andExpect(status().isInternalServerError());
    }
}
