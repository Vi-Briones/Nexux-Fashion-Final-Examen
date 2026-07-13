package com.Nexus_Fashion.resena_service.ResenaControllerTest;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.Nexus_Fashion.resena_service.assemblers.ResenaModelAssembler;
import com.Nexus_Fashion.resena_service.controller.ResenaControllerV2;
import com.Nexus_Fashion.resena_service.dto.ResenaDTO;
import com.Nexus_Fashion.resena_service.exception.GlobalExceptionHandler;
import com.Nexus_Fashion.resena_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.resena_service.service.ResenaService;

@ExtendWith(MockitoExtension.class)
public class ResenaControllerV2Test {

     private MockMvc mockMvc;

    @Mock
    private ResenaService resenaService;

    @Mock
    private ResenaModelAssembler assembler;

    private ResenaDTO resenaDTOEjemplo;

    @BeforeEach
    void setUp() {
        ResenaControllerV2 controller = new ResenaControllerV2(resenaService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        resenaDTOEjemplo = new ResenaDTO(1L, 100L, "Juan Perez", 5, "Excelente producto");
    }

    @Test
    void testListarResenas_retornaListaHateoas() throws Exception {
        EntityModel<ResenaDTO> entityModel = EntityModel.of(resenaDTOEjemplo);
        when(resenaService.listar()).thenReturn(Arrays.asList(resenaDTOEjemplo));
        when(assembler.toModel(resenaDTOEjemplo)).thenReturn(entityModel);

        mockMvc.perform(get("/resenas/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(resenaService, times(1)).listar();
        verify(assembler, times(1)).toModel(resenaDTOEjemplo);
    }

    @Test
    void testListarResenas_listaVacia_retorna200() throws Exception {
        when(resenaService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/resenas/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(assembler, never()).toModel(any());
    }

    @Test
    void testObtenerResena_encontrado_retorna200() throws Exception {
        EntityModel<ResenaDTO> entityModel = EntityModel.of(resenaDTOEjemplo);
        when(resenaService.obtenerPorId(1L)).thenReturn(resenaDTOEjemplo);
        when(assembler.toModel(resenaDTOEjemplo)).thenReturn(entityModel);

        mockMvc.perform(get("/resenas/v2/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // A diferencia de InventarioControllerV2, este solo llama obtenerPorId una vez
        verify(resenaService, times(1)).obtenerPorId(1L);
        verify(assembler, times(1)).toModel(resenaDTOEjemplo);
    }

    @Test
    void testObtenerResena_noEncontrado_retorna404() throws Exception {
        when(resenaService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Reseña no encontrada con ID: 99"));

        mockMvc.perform(get("/resenas/v2/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
