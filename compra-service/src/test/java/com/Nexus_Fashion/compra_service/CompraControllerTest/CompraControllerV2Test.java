package com.Nexus_Fashion.compra_service.CompraControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.Nexus_Fashion.compra_service.assemblers.CompraModelAssembler;
import com.Nexus_Fashion.compra_service.controller.CompraControllerV2;
import com.Nexus_Fashion.compra_service.dto.CompraDTO;
import com.Nexus_Fashion.compra_service.model.Compra;
import com.Nexus_Fashion.compra_service.model.DetalleCompra;
import com.Nexus_Fashion.compra_service.service.CompraService;

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
public class CompraControllerV2Test {

    private MockMvc mockMvc;

    @Mock
    private CompraService compraService;

    @Mock
    private CompraModelAssembler assembler;

    private Compra compraEjemplo;
    private CompraDTO compraDtoEjemplo;

    @BeforeEach
    void setUp() {
        CompraControllerV2 controller = new CompraControllerV2(compraService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Construir entidad Compra de ejemplo
        DetalleCompra detalle = new DetalleCompra();
        detalle.setIdProducto(10L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(25000.0);

        compraEjemplo = new Compra();
        compraEjemplo.setId(1L);
        compraEjemplo.setIdCliente(5L);
        compraEjemplo.setTotal(50000.0);
        compraEjemplo.setDetalles(Collections.singletonList(detalle));

        // DTO equivalente (usado para verificar respuesta del assembler)
        compraDtoEjemplo = new CompraDTO(
                1L, 5L, 10L, 2, 25000.0, 50000.0
        );
    }

    @Test
    void testListarCompras_retornaListaHateoas() throws Exception {
        EntityModel<CompraDTO> entityModel = EntityModel.of(compraDtoEjemplo);
        when(compraService.listar()).thenReturn(Arrays.asList(compraEjemplo));
        when(assembler.toModel(compraEjemplo)).thenReturn(entityModel);

        mockMvc.perform(get("/v2/compras")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(compraService, times(1)).listar();
        verify(assembler, times(1)).toModel(compraEjemplo);
    }

    @Test
    void testListarCompras_listaVacia_retorna200() throws Exception {
        when(compraService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v2/compras")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(compraService, times(1)).listar();
        verify(assembler, never()).toModel(any());
    }

    @Test
    void testObtenerCompra_encontrado_retorna200() throws Exception {
        EntityModel<CompraDTO> entityModel = EntityModel.of(compraDtoEjemplo);
        when(compraService.buscarPorId(1L)).thenReturn(compraEjemplo);
        when(assembler.toModel(compraEjemplo)).thenReturn(entityModel);

        mockMvc.perform(get("/v2/compras/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(compraService, times(1)).buscarPorId(1L);
        verify(assembler, times(1)).toModel(compraEjemplo);
    }

    @Test
    void testObtenerCompra_noEncontrado_retorna404() throws Exception {
        when(compraService.buscarPorId(99L)).thenReturn(null);

        mockMvc.perform(get("/v2/compras/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(compraService, times(1)).buscarPorId(99L);
    }
}