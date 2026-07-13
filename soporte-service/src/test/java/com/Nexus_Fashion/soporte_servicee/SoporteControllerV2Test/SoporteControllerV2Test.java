package com.Nexus_Fashion.soporte_servicee.SoporteControllerV2Test;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.Nexus_Fashion.soporte_service.assemblers.SoporteModelAssembler;
import com.Nexus_Fashion.soporte_service.controller.SoporteControllerV2;
import com.Nexus_Fashion.soporte_service.exepcion.GlobalExceptionHandler;
import com.Nexus_Fashion.soporte_service.model.Soporte;
import com.Nexus_Fashion.soporte_service.service.SoporteService;

@ExtendWith(MockitoExtension.class)
public class SoporteControllerV2Test {

    private MockMvc mockMvc;

    @Mock
    private SoporteService soporteService;

    @Mock
    private SoporteModelAssembler assembler;

    private Soporte soporteEjemplo;

    @BeforeEach
    void setUp() {
        SoporteControllerV2 controller = new SoporteControllerV2(soporteService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        soporteEjemplo = new Soporte(1L, 10L, "Problema con el login", "No puedo ingresar a mi cuenta",
                "PENDIENTE", "ALTA", LocalDateTime.now(), null);
    }

    @Test
    void testListarSoportes_retornaListaHateoas() throws Exception {
        EntityModel<Soporte> entityModel = EntityModel.of(soporteEjemplo);
        when(soporteService.listar()).thenReturn(Arrays.asList(soporteEjemplo));
        when(assembler.toModel(soporteEjemplo)).thenReturn(entityModel);

        mockMvc.perform(get("/soportes/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(soporteService, times(1)).listar();
        verify(assembler, times(1)).toModel(soporteEjemplo);
    }

    @Test
    void testListarSoportes_listaVacia_retorna200() throws Exception {
        when(soporteService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/soportes/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(assembler, never()).toModel(any());
    }

    @Test
    void testObtenerSoporte_encontrado_retorna200() throws Exception {
        EntityModel<Soporte> entityModel = EntityModel.of(soporteEjemplo);
        when(soporteService.obtenerPorId(1L)).thenReturn(soporteEjemplo);
        when(assembler.toModel(soporteEjemplo)).thenReturn(entityModel);

        mockMvc.perform(get("/soportes/v2/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(soporteService, times(1)).obtenerPorId(1L);
        verify(assembler, times(1)).toModel(soporteEjemplo);
    }

    @Test
    void testObtenerSoporte_noEncontrado_retorna500() throws Exception {
        when(soporteService.obtenerPorId(99L))
                .thenThrow(new RuntimeException("Ticket de soporte no existe"));

        mockMvc.perform(get("/soportes/v2/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }    
}
