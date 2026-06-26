package com.Nexus_Fashion.venta_service.VentaControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.Nexus_Fashion.venta_service.controller.VentaController;
import com.Nexus_Fashion.venta_service.dto.VentaDTO;
import com.Nexus_Fashion.venta_service.service.VentaService;
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
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class VentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VentaService ventaService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private VentaDTO ventaDtoRespuesta;   // lo que devuelve el servicio (tiene idVenta)
    private VentaDTO ventaDtoRequest;     // lo que envía el cliente (sin idVenta)

    @BeforeEach
    void setUp() {
        VentaController controller = new VentaController(ventaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        VentaDTO.DetalleItem detalle = new VentaDTO.DetalleItem(10L, 2);

        ventaDtoRespuesta = new VentaDTO(
                1L,
                5L,
                new Date(),
                50000.0,
                1L,
                Collections.singletonList(detalle)
        );

        ventaDtoRequest = new VentaDTO(
                null,
                5L,
                new Date(),
                50000.0,
                1L,
                Collections.singletonList(detalle)
        );
    }

    @Test
    void testCrearVenta_retorna201() throws Exception {
        when(ventaService.guardar(any(VentaDTO.class))).thenReturn(ventaDtoRespuesta);

        mockMvc.perform(post("/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ventaDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVenta").value(1L))
                .andExpect(jsonPath("$.total").value(50000.0))
                .andExpect(jsonPath("$.idMetodoPago").value(1L));
    }

    @Test
    void testListarVentas_retornaLista() throws Exception {
        when(ventaService.listar()).thenReturn(Arrays.asList(ventaDtoRespuesta));

        mockMvc.perform(get("/ventas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idVenta").value(1L))
                .andExpect(jsonPath("$[0].total").value(50000.0))
                .andExpect(jsonPath("$[0].idMetodoPago").value(1L));
    }

    @Test
    void testListarVentas_listaVacia_retorna200() throws Exception {
        when(ventaService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/ventas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testObtenerVenta_encontrado_retorna200() throws Exception {
        when(ventaService.buscarPorId(1L)).thenReturn(ventaDtoRespuesta);

        mockMvc.perform(get("/ventas/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(1L))
                .andExpect(jsonPath("$.total").value(50000.0));
    }

    @Test
    void testObtenerVenta_noEncontrado_retorna404() throws Exception {
        when(ventaService.buscarPorId(99L)).thenReturn(null);

        mockMvc.perform(get("/ventas/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarVenta_exitoso_retorna200() throws Exception {
        VentaDTO dtoActualizado = new VentaDTO(1L, 5L, new Date(), 75000.0, 1L,
                Collections.emptyList());
        when(ventaService.actualizar(eq(1L), any(VentaDTO.class))).thenReturn(dtoActualizado);

        mockMvc.perform(put("/ventas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ventaDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(1L))
                .andExpect(jsonPath("$.total").value(75000.0));
    }

    @Test
    void testActualizarVenta_noExiste_retorna404() throws Exception {
        when(ventaService.actualizar(eq(99L), any(VentaDTO.class)))
                .thenThrow(new RuntimeException("Venta no encontrada"));

        mockMvc.perform(put("/ventas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ventaDtoRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarVenta_exitoso_retorna204() throws Exception {
        doNothing().when(ventaService).eliminar(1L);

        mockMvc.perform(delete("/ventas/1"))
                .andExpect(status().isNoContent());

        verify(ventaService, times(1)).eliminar(1L);
    }

    @Test
    void testEliminarVenta_noExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Venta no encontrada"))
                .when(ventaService).eliminar(99L);

        mockMvc.perform(delete("/ventas/99"))
                .andExpect(status().isNotFound());
    }
}
