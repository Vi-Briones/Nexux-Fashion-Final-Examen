package com.Nexus_Fashion.producto_service.ProductoControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
import com.Nexus_Fashion.producto_service.controller.ProductoController;
import com.Nexus_Fashion.producto_service.dto.ProductoDTO;
import com.Nexus_Fashion.producto_service.model.Categoria;
import com.Nexus_Fashion.producto_service.model.Producto;
import com.Nexus_Fashion.producto_service.service.ProductoService;
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
 
@ExtendWith(MockitoExtension.class)
public class ProductoControllerTest {

    private MockMvc mockMvc;
 
    @Mock
    private ProductoService productoService;
 
    private ObjectMapper objectMapper;
 
    private Producto productoEjemplo;
    private ProductoDTO productoDtoRespuesta;  // lo que devuelve el servicio (tiene idProducto)
    private ProductoDTO productoDtoRequest;    // lo que envía el cliente (sin idProducto)
 
    @BeforeEach
    void setUp() {
        ProductoController controller = new ProductoController(productoService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
 
        Categoria categoria = new Categoria(1L, "Ropa");
 
        productoEjemplo = new Producto(1L, "Polera Negra", 9990.0, 50, categoria);
 
        productoDtoRespuesta = new ProductoDTO(1L, "Polera Negra", 9990.0, 50, 1L);
        productoDtoRequest = new ProductoDTO(null, "Polera Negra", 9990.0, 50, 1L);
    }
 
    @Test
    void testCrearProducto_retorna201() throws Exception {
        when(productoService.guardar(any(Producto.class))).thenReturn(productoEjemplo);
 
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombre").value("Polera Negra"))
                .andExpect(jsonPath("$.precio").value(9990.0));
    }
 
    @Test
    void testListarProductos_retornaLista() throws Exception {
        when(productoService.listar()).thenReturn(Arrays.asList(productoEjemplo));
 
        mockMvc.perform(get("/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProducto").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Polera Negra"));
    }
 
    @Test
    void testListarProductos_listaVacia_retorna200() throws Exception {
        when(productoService.listar()).thenReturn(Collections.emptyList());
 
        mockMvc.perform(get("/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
 
    @Test
    void testObtenerProducto_encontrado_retorna200() throws Exception {
        when(productoService.buscarPorId(1L)).thenReturn(productoEjemplo);
 
        mockMvc.perform(get("/productos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombre").value("Polera Negra"));
    }
 
    @Test
    void testObtenerProducto_noEncontrado_retorna404() throws Exception {
        when(productoService.buscarPorId(99L)).thenReturn(null);
 
        mockMvc.perform(get("/productos/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
 
    @Test
    void testActualizarProducto_exitoso_retorna200() throws Exception {
        Producto productoActualizado = new Producto(1L, "Polera Blanca", 12990.0, 30, productoEjemplo.getCategoria());
        when(productoService.actualizar(eq(1L), any(Producto.class))).thenReturn(productoActualizado);
 
        mockMvc.perform(put("/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value(1L))
                .andExpect(jsonPath("$.nombre").value("Polera Blanca"))
                .andExpect(jsonPath("$.precio").value(12990.0));
    }
 
    @Test
    void testActualizarProducto_noExiste_retorna404() throws Exception {
        when(productoService.actualizar(eq(99L), any(Producto.class)))
                .thenThrow(new RuntimeException("Producto no encontrado"));
 
        mockMvc.perform(put("/productos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productoDtoRequest)))
                .andExpect(status().isNotFound());
    }
 
    @Test
    void testEliminarProducto_exitoso_retorna204() throws Exception {
        doNothing().when(productoService).eliminar(1L);
 
        mockMvc.perform(delete("/productos/1"))
                .andExpect(status().isNoContent());
 
        verify(productoService, times(1)).eliminar(1L);
    }
 
    @Test
    void testEliminarProducto_noExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Producto no encontrado"))
                .when(productoService).eliminar(99L);
 
        mockMvc.perform(delete("/productos/99"))
                .andExpect(status().isNotFound());
    }
}
