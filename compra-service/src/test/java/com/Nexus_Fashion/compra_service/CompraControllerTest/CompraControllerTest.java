package com.Nexus_Fashion.compra_service.CompraControllerTest;

import com.Nexus_Fashion.compra_service.controller.CompraController;
import com.Nexus_Fashion.compra_service.dto.CompraDTO;
import com.Nexus_Fashion.compra_service.exception.GlobalExceptionHandler;
import com.Nexus_Fashion.compra_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.compra_service.model.Compra;
import com.Nexus_Fashion.compra_service.model.DetalleCompra;
import com.Nexus_Fashion.compra_service.service.CompraService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CompraControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CompraService compraService;

    private ObjectMapper objectMapper;

    private Compra compraEjemplo;
    private CompraDTO compraDtoRequest;

    @BeforeEach
    void setUp() {
        CompraController controller = new CompraController(compraService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        DetalleCompra detalle = new DetalleCompra();
        detalle.setIdProducto(10L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(25000.0);

        compraEjemplo = new Compra();
        compraEjemplo.setId(1L);
        compraEjemplo.setIdCliente(100L);
        compraEjemplo.setTotal(50000.0);
        compraEjemplo.setDetalles(Collections.singletonList(detalle));

        compraDtoRequest = new CompraDTO(null, 100L, 10L, 2, 25000.0, 50000.0);
    }

    @Test
    void testCrearCompra_retorna201() throws Exception {
        when(compraService.guardar(any(Compra.class))).thenReturn(compraEjemplo);

        mockMvc.perform(post("/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compraDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.idCliente").value(100))
                .andExpect(jsonPath("$.idProducto").value(10));

        verify(compraService, times(1)).guardar(any(Compra.class));
    }

    @Test
    void testListarCompras_retornaLista() throws Exception {
        when(compraService.listar()).thenReturn(Collections.singletonList(compraEjemplo));

        mockMvc.perform(get("/compras")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].idCliente").value(100));

        verify(compraService, times(1)).listar();
    }

    @Test
    void testObtenerCompra_encontrada_retorna200() throws Exception {
        when(compraService.buscarPorId(1L)).thenReturn(compraEjemplo);

        mockMvc.perform(get("/compras/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(compraService, times(1)).buscarPorId(1L);
    }

    @Test
    void testObtenerCompra_noEncontrada_retorna404() throws Exception {
        when(compraService.buscarPorId(99L)).thenReturn(null);

        mockMvc.perform(get("/compras/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(compraService, times(1)).buscarPorId(99L);
    }

    @Test
    void testExisteCompra_retornaTrue() throws Exception {
        when(compraService.existePorId(1L)).thenReturn(true);

        mockMvc.perform(get("/compras/1/exists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(compraService, times(1)).existePorId(1L);
    }

    @Test
    void testExisteCompra_retornaFalse() throws Exception {
        when(compraService.existePorId(2L)).thenReturn(false);

        mockMvc.perform(get("/compras/2/exists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(compraService, times(1)).existePorId(2L);
    }

    @Test
    void testActualizarCompra_exitosa_retorna200() throws Exception {
        Compra compraActualizada = new Compra();
        compraActualizada.setId(1L);
        compraActualizada.setIdCliente(100L);
        compraActualizada.setTotal(60000.0);
        compraActualizada.setDetalles(compraEjemplo.getDetalles());

        when(compraService.actualizar(eq(1L), any(Compra.class))).thenReturn(compraActualizada);

        mockMvc.perform(put("/compras/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compraDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.total").value(60000.0));

        verify(compraService, times(1)).actualizar(eq(1L), any(Compra.class));
    }

    @Test
    void testActualizarCompra_noExiste_retorna404() throws Exception {
        when(compraService.actualizar(eq(99L), any(Compra.class)))
                .thenThrow(new ResourceNotFoundException("Compra no encontrada"));

        mockMvc.perform(put("/compras/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compraDtoRequest)))
                .andExpect(status().isNotFound());

        verify(compraService, times(1)).actualizar(eq(99L), any(Compra.class));
    }

    @Test
    void testEliminarCompra_exitosa_retorna204() throws Exception {
        doNothing().when(compraService).eliminar(1L);

        mockMvc.perform(delete("/compras/1"))
                .andExpect(status().isNoContent());

        verify(compraService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminarCompra_noExiste_retorna404() throws Exception {
        doThrow(new ResourceNotFoundException("Compra no encontrada")).when(compraService).eliminar(99L);

        mockMvc.perform(delete("/compras/99"))
                .andExpect(status().isNotFound());

        verify(compraService, times(1)).eliminar(99L);
    }

    @Test
    void testListarComprasPorCliente_retornaLista() throws Exception {
        when(compraService.listarPorCliente(100L)).thenReturn(Collections.singletonList(compraEjemplo));

        mockMvc.perform(get("/compras/cliente/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCliente").value(100));

        verify(compraService, times(1)).listarPorCliente(100L);
    }

    @Test
    void testTotalComprasPorCliente_retornaValor() throws Exception {
        when(compraService.totalComprasPorCliente(100L)).thenReturn(3L);

        mockMvc.perform(get("/compras/cliente/100/total")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

        verify(compraService, times(1)).totalComprasPorCliente(100L);
    }
}