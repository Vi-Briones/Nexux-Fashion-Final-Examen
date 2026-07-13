package com.Nexus_Fashion.resena_service.ResenaControllerTest;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.Nexus_Fashion.resena_service.controller.ResenaController;
import com.Nexus_Fashion.resena_service.dto.ResenaDTO;
import com.Nexus_Fashion.resena_service.exception.GlobalExceptionHandler;
import com.Nexus_Fashion.resena_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.resena_service.service.ResenaService;

@ExtendWith(MockitoExtension.class)
public class ResenaControllerTest {

    private MockMvc mockMvc; 

    @Mock
    private ResenaService resenaService;

    private ObjectMapper objectMapper;

    private ResenaDTO resenaDTOEjemplo;
    private ResenaDTO resenaDTORequest;

    @BeforeEach
    void setUp() {
        ResenaController controller = new ResenaController(resenaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        resenaDTOEjemplo = new ResenaDTO(1L, 100L, "Juan Perez", 5, "Excelente producto");
        resenaDTORequest = new ResenaDTO(null, 100L, "Juan Perez", 5, "Excelente producto");
    }

    // ---------- guardar ----------

    @Test
    void testGuardar_retorna201() throws Exception {
        when(resenaService.guardar(any(ResenaDTO.class))).thenReturn(resenaDTOEjemplo);

        mockMvc.perform(post("/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resenaDTORequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cliente").value("Juan Perez"))
                .andExpect(jsonPath("$.calificacion").value(5));
    }

    @Test
    void testGuardar_servicioLanzaExcepcion_retorna500() throws Exception {
        when(resenaService.guardar(any(ResenaDTO.class)))
                .thenThrow(new RuntimeException("Fallo en POST: la compra no existe"));

        mockMvc.perform(post("/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resenaDTORequest)))
                .andExpect(status().isInternalServerError());
    }

    // ---------- listar ----------

    @Test
    void testListar_retornaLista() throws Exception {
        when(resenaService.listar()).thenReturn(Arrays.asList(resenaDTOEjemplo));

        mockMvc.perform(get("/resenas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].cliente").value("Juan Perez"));
    }

    @Test
    void testListar_listaVacia_retorna200() throws Exception {
        when(resenaService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/resenas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ---------- obtenerPorId ----------

    @Test
    void testObtenerPorId_encontrado_retorna200() throws Exception {
        when(resenaService.obtenerPorId(1L)).thenReturn(resenaDTOEjemplo);

        mockMvc.perform(get("/resenas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testObtenerPorId_noEncontrado_retorna404() throws Exception {
        when(resenaService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Reseña no encontrada con ID: 99"));

        mockMvc.perform(get("/resenas/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // ---------- actualizar ----------

    @Test
    void testActualizar_exitoso_retorna200() throws Exception {
        ResenaDTO actualizado = new ResenaDTO(1L, 100L, "Juan Perez", 4, "Bueno, pero podría mejorar");
        when(resenaService.actualizar(eq(1L), any(ResenaDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/resenas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calificacion").value(4));
    }

    @Test
    void testActualizar_noExiste_retorna404() throws Exception {
        when(resenaService.actualizar(eq(99L), any(ResenaDTO.class)))
                .thenThrow(new ResourceNotFoundException("La reseña con ID 99 no existe"));

        mockMvc.perform(put("/resenas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resenaDTORequest)))
                .andExpect(status().isNotFound());
    }

    // ---------- eliminar ----------

    @Test
    void testEliminar_exitoso_retorna204() throws Exception {
        doNothing().when(resenaService).eliminar(1L);

        mockMvc.perform(delete("/resenas/1"))
                .andExpect(status().isNoContent());

        verify(resenaService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminar_noExiste_retorna404() throws Exception {
        doThrow(new ResourceNotFoundException("Reseña no encontrada para eliminar con ID: 99"))
                .when(resenaService).eliminar(99L);

        mockMvc.perform(delete("/resenas/99"))
                .andExpect(status().isNotFound());
    }
}
