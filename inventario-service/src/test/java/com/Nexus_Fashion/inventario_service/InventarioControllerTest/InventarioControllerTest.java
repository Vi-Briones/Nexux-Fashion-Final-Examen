package com.Nexus_Fashion.inventario_service.InventarioControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 
import com.Nexus_Fashion.inventario_service.controller.InventarioController;
import com.Nexus_Fashion.inventario_service.dto.InventarioDTO;
import com.Nexus_Fashion.inventario_service.exception.GlobalExceptionHandler;
import com.Nexus_Fashion.inventario_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.inventario_service.model.Inventario;
import com.Nexus_Fashion.inventario_service.service.InventarioService;
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
public class InventarioControllerTest {

    private MockMvc mockMvc;
 
    @Mock
    private InventarioService inventarioService;
 
    private ObjectMapper objectMapper;
 
    private Inventario inventarioEjemplo;
    private InventarioDTO inventarioDtoRequest;
 
    @BeforeEach
    void setUp() {
        InventarioController controller = new InventarioController(inventarioService);
        // Se registra el GlobalExceptionHandler para que las excepciones del service
        // (RuntimeException, ResourceNotFoundException) se traduzcan en los status HTTP correctos
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
 
        inventarioEjemplo = Inventario.builder()
                .id(1L)
                .idProducto(10L)
                .sku("NEXUS-POLERA-100")
                .cantidadDisponible(50)
                .ubicacionBodega("Pasillo A - Estante 1")
                .build();
 
        inventarioDtoRequest = InventarioDTO.builder()
                .idProducto(10L)
                .sku("NEXUS-POLERA-100")
                .cantidadDisponible(50)
                .ubicacionBodega("Pasillo A - Estante 1")
                .build();
    }
 
    // ---------- crearInventario ----------
 
    @Test
    void testCrearInventario_retorna200() throws Exception {
        when(inventarioService.guardar(any(Inventario.class))).thenReturn(inventarioEjemplo);
 
        mockMvc.perform(post("/inventarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventarioDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.sku").value("NEXUS-POLERA-100"))
                .andExpect(jsonPath("$.cantidadDisponible").value(50));
    }
 
    @Test
    void testCrearInventario_servicioLanzaExcepcion_retorna400() throws Exception {
        when(inventarioService.guardar(any(Inventario.class)))
                .thenThrow(new IllegalArgumentException("La cantidad disponible no puede ser menor a cero"));
 
        mockMvc.perform(post("/inventarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventarioDtoRequest)))
                .andExpect(status().isInternalServerError());
    }
 
    // ---------- listarInventarios ----------
 
    @Test
    void testListarInventarios_retornaLista() throws Exception {
        when(inventarioService.listar()).thenReturn(Arrays.asList(inventarioEjemplo));
 
        mockMvc.perform(get("/inventarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].sku").value("NEXUS-POLERA-100"));
    }
 
    @Test
    void testListarInventarios_listaVacia_retorna200() throws Exception {
        when(inventarioService.listar()).thenReturn(Collections.emptyList());
 
        mockMvc.perform(get("/inventarios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
 
    // ---------- obtenerInventario ----------
 
    @Test
    void testObtenerInventario_encontrado_retorna200() throws Exception {
        when(inventarioService.obtenerPorId(1L)).thenReturn(inventarioEjemplo);
 
        mockMvc.perform(get("/inventarios/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
 
    @Test
    void testObtenerInventario_noEncontrado_retorna404() throws Exception {
        when(inventarioService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Registro de inventario no encontrado"));
 
        mockMvc.perform(get("/inventarios/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
 
    // ---------- actualizarInventario ----------
 
    @Test
    void testActualizarInventario_exitoso_retorna200() throws Exception {
        Inventario actualizado = Inventario.builder()
                .id(1L)
                .idProducto(10L)
                .sku("NEXUS-POLERA-200")
                .cantidadDisponible(80)
                .ubicacionBodega("Pasillo B - Estante 2")
                .build();
 
        when(inventarioService.obtenerPorId(1L)).thenReturn(inventarioEjemplo);
        when(inventarioService.guardar(any(Inventario.class))).thenReturn(actualizado);
 
        InventarioDTO dtoActualizado = InventarioDTO.builder()
                .idProducto(10L)
                .sku("NEXUS-POLERA-200")
                .cantidadDisponible(80)
                .ubicacionBodega("Pasillo B - Estante 2")
                .build();
 
        mockMvc.perform(put("/inventarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("NEXUS-POLERA-200"))
                .andExpect(jsonPath("$.cantidadDisponible").value(80));
    }
 
    @Test
    void testActualizarInventario_noExiste_retorna404() throws Exception {
        when(inventarioService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Registro de inventario no encontrado"));
 
        mockMvc.perform(put("/inventarios/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventarioDtoRequest)))
                .andExpect(status().isNotFound());
    }
 
    // ---------- eliminarInventario ----------
 
    @Test
    void testEliminarInventario_exitoso_retorna200() throws Exception {
        doNothing().when(inventarioService).eliminar(1L);
 
        mockMvc.perform(delete("/inventarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventario Eliminado Exitosamente"));
 
        verify(inventarioService, times(1)).eliminar(1L);
    }
 
    @Test
    void testEliminarInventario_noExiste_retorna404() throws Exception {
        doThrow(new ResourceNotFoundException("Registro de inventario no encontrado"))
                .when(inventarioService).eliminar(99L);
 
        mockMvc.perform(delete("/inventarios/99"))
                .andExpect(status().isNotFound());
    }
}
