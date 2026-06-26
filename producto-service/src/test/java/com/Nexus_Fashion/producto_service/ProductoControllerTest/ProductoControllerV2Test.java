package com.Nexus_Fashion.producto_service.ProductoControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.Nexus_Fashion.producto_service.assemblers.ProductoModelAssembler;
import com.Nexus_Fashion.producto_service.controller.ProductoControllerV2;
import com.Nexus_Fashion.producto_service.dto.ProductoDTO;
import com.Nexus_Fashion.producto_service.model.Categoria;
import com.Nexus_Fashion.producto_service.model.Producto;
import com.Nexus_Fashion.producto_service.service.ProductoService;
 
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
public class ProductoControllerV2Test {

    private MockMvc mockMvc;
 
    @Mock
    private ProductoService productoService;
 
    @Mock
    private ProductoModelAssembler assembler;
 
    private Producto productoEjemplo;
    private ProductoDTO productoDtoEjemplo;
 
    @BeforeEach
    void setUp() {
        ProductoControllerV2 controller = new ProductoControllerV2(productoService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
 
        Categoria categoria = new Categoria(1L, "Ropa");
        productoEjemplo = new Producto(1L, "Polera Negra", 9990.0, 50, categoria);
        productoDtoEjemplo = new ProductoDTO(1L, "Polera Negra", 9990.0, 50, 1L);
    }
 
    @Test
    void testListarProductos_retornaListaHateoas() throws Exception {
        EntityModel<ProductoDTO> entityModel = EntityModel.of(productoDtoEjemplo);
        when(productoService.listar()).thenReturn(Arrays.asList(productoEjemplo));
        when(assembler.toModel(productoEjemplo)).thenReturn(entityModel);
 
        mockMvc.perform(get("/v2/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(productoService, times(1)).listar();
        verify(assembler, times(1)).toModel(productoEjemplo);
    }
 
    @Test
    void testListarProductos_listaVacia_retorna200() throws Exception {
        when(productoService.listar()).thenReturn(Collections.emptyList());
 
        mockMvc.perform(get("/v2/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(productoService, times(1)).listar();
        verify(assembler, never()).toModel(any());
    }
 
    @Test
    void testObtenerProducto_encontrado_retorna200() throws Exception {
        EntityModel<ProductoDTO> entityModel = EntityModel.of(productoDtoEjemplo);
        when(productoService.buscarPorId(1L)).thenReturn(productoEjemplo);
        when(assembler.toModel(productoEjemplo)).thenReturn(entityModel);
 
        mockMvc.perform(get("/v2/productos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(productoService, times(1)).buscarPorId(1L);
        verify(assembler, times(1)).toModel(productoEjemplo);
    }
 
    @Test
    void testObtenerProducto_noEncontrado_retorna404() throws Exception {
        when(productoService.buscarPorId(99L)).thenReturn(null);
 
        mockMvc.perform(get("/v2/productos/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
