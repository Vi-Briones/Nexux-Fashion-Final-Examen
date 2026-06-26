package com.Nexus_Fashion.venta_service.VentaControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
import com.Nexus_Fashion.venta_service.assemblers.VentaModelAssembler;
import com.Nexus_Fashion.venta_service.controller.VentaControllerV2;
import com.Nexus_Fashion.venta_service.dto.VentaDTO;
import com.Nexus_Fashion.venta_service.service.VentaService;

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
import java.util.Date;
 
@ExtendWith(MockitoExtension.class)
public class VentaControllerV2Test {

    private MockMvc mockMvc;
 
    @Mock
    private VentaService ventaService;
 
    @Mock
    private VentaModelAssembler assembler;
 
    private VentaDTO ventaDtoEjemplo;
 
    @BeforeEach
    void setUp() {
        VentaControllerV2 controller = new VentaControllerV2(ventaService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
 
        VentaDTO.DetalleItem detalle = new VentaDTO.DetalleItem(10L, 2);
        ventaDtoEjemplo = new VentaDTO(
                1L, 5L, new Date(), 50000.0, 1L,
                Collections.singletonList(detalle)
        );
    }
 
    @Test
    void testListarVentas_retornaListaHateoas() throws Exception {
        EntityModel<VentaDTO> entityModel = EntityModel.of(ventaDtoEjemplo);
        when(ventaService.listar()).thenReturn(Arrays.asList(ventaDtoEjemplo));
        when(assembler.toModel(ventaDtoEjemplo)).thenReturn(entityModel);
 
        mockMvc.perform(get("/v2/ventas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(ventaService, times(1)).listar();
        verify(assembler, times(1)).toModel(ventaDtoEjemplo);
    }
 
    @Test
    void testListarVentas_listaVacia_retorna200() throws Exception {
        when(ventaService.listar()).thenReturn(Collections.emptyList());
 
        mockMvc.perform(get("/v2/ventas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(ventaService, times(1)).listar();
        verify(assembler, never()).toModel(any());
    }
 
    @Test
    void testObtenerVenta_encontrado_retorna200() throws Exception {
        EntityModel<VentaDTO> entityModel = EntityModel.of(ventaDtoEjemplo);
        when(ventaService.buscarPorId(1L)).thenReturn(ventaDtoEjemplo);
        when(assembler.toModel(ventaDtoEjemplo)).thenReturn(entityModel);
 
        mockMvc.perform(get("/v2/ventas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(ventaService, times(1)).buscarPorId(1L);
        verify(assembler, times(1)).toModel(ventaDtoEjemplo);
    }
 
    @Test
    void testObtenerVenta_noEncontrado_retorna404() throws Exception {
        when(ventaService.buscarPorId(99L)).thenReturn(null);
 
        mockMvc.perform(get("/v2/ventas/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
 
    }   
}
