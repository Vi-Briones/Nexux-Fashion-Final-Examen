package com.Nexus_Fashion.inventario_service.InventarioControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
import com.Nexus_Fashion.inventario_service.assemblers.InventarioModelAssembler;
import com.Nexus_Fashion.inventario_service.controller.InventarioControllerV2;
import com.Nexus_Fashion.inventario_service.exception.GlobalExceptionHandler;
import com.Nexus_Fashion.inventario_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.inventario_service.model.Inventario;
import com.Nexus_Fashion.inventario_service.service.InventarioService;
 
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
public class InventarioControllerV2Test {

    private MockMvc mockMvc;
 
    @Mock
    private InventarioService inventarioService;
 
    @Mock
    private InventarioModelAssembler assembler;
 
    private Inventario inventarioEjemplo;
 
    @BeforeEach
    void setUp() {
        InventarioControllerV2 controller = new InventarioControllerV2(inventarioService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
 
        inventarioEjemplo = Inventario.builder()
                .id(1L)
                .idProducto(10L)
                .sku("NEXUS-POLERA-100")
                .cantidadDisponible(50)
                .ubicacionBodega("Pasillo A - Estante 1")
                .build();
    }
 
    @Test
    void testListarInventarios_retornaListaHateoas() throws Exception {
        EntityModel<Inventario> entityModel = EntityModel.of(inventarioEjemplo);
        when(inventarioService.listar()).thenReturn(Arrays.asList(inventarioEjemplo));
        when(assembler.toModel(inventarioEjemplo)).thenReturn(entityModel);
 
        mockMvc.perform(get("/inventarios/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(inventarioService, times(1)).listar();
        verify(assembler, times(1)).toModel(inventarioEjemplo);
    }
 
    @Test
    void testListarInventarios_listaVacia_retorna200() throws Exception {
        when(inventarioService.listar()).thenReturn(Collections.emptyList());
 
        mockMvc.perform(get("/inventarios/v2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(inventarioService, times(1)).listar();
        verify(assembler, never()).toModel(any());
    }
 
    @Test
    void testObtenerInventario_encontrado_retorna200() throws Exception {
        EntityModel<Inventario> entityModel = EntityModel.of(inventarioEjemplo);
        when(inventarioService.obtenerPorId(1L)).thenReturn(inventarioEjemplo);
        when(assembler.toModel(inventarioEjemplo)).thenReturn(entityModel);
 
        mockMvc.perform(get("/inventarios/v2/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        // obtenerPorId se llama dos veces porque el controller lo invoca
        // una vez para el chequeo != null y otra dentro del assembler.toModel(...)
        verify(inventarioService, times(2)).obtenerPorId(1L);
        verify(assembler, times(1)).toModel(inventarioEjemplo);
    }
 
    @Test
    void testObtenerInventario_noEncontrado_retorna404() throws Exception {
        when(inventarioService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Registro de inventario no encontrado"));
 
        mockMvc.perform(get("/inventarios/v2/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
