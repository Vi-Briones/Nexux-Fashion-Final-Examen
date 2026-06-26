package com.Nexus_Fashion.cliente_service.ClienteControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.Nexus_Fashion.cliente_service.controller.ClienteController;
import com.Nexus_Fashion.cliente_service.dto.ClienteDTO;
import com.Nexus_Fashion.cliente_service.model.Cliente;
import com.Nexus_Fashion.cliente_service.model.Rol;
import com.Nexus_Fashion.cliente_service.service.ClienteService;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class ClienteControllerTest {

    private MockMvc mockMvc;
 
    @Mock
    private ClienteService clienteService;
 
    private ObjectMapper objectMapper;
    private Cliente clienteEjemplo;
    private ClienteDTO clienteDtoRequest;
 
    @BeforeEach
    void setUp() {
        ClienteController controller = new ClienteController(clienteService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
 
        Rol rol = new Rol(1L, "CLIENTE");
 
        clienteEjemplo = new Cliente(1L, "Juan Pérez", "juan@correo.com", "password123", rol);
 
        clienteDtoRequest = new ClienteDTO(null, "Juan Pérez", "juan@correo.com", "password123", 1L);
    }

    @Test
    void testCrearCliente_retorna201() throws Exception {
        when(clienteService.guardar(any(Cliente.class))).thenReturn(clienteEjemplo);
 
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.correo").value("juan@correo.com"))
                .andExpect(jsonPath("$.idRol").value(1L));
    }

    @Test
    void testListarClientes_retornaLista() throws Exception {
        when(clienteService.listar()).thenReturn(Arrays.asList(clienteEjemplo));
 
        mockMvc.perform(get("/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCliente").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[0].correo").value("juan@correo.com"));
    }
 
    @Test
    void testListarClientes_listaVacia_retorna200() throws Exception {
        when(clienteService.listar()).thenReturn(Collections.emptyList());
 
        mockMvc.perform(get("/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testObtenerCliente_encontrado_retorna200() throws Exception {
        when(clienteService.buscarPorId(1L)).thenReturn(clienteEjemplo);
 
        mockMvc.perform(get("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1L))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"));
    }
 
    @Test
    void testObtenerCliente_noEncontrado_retorna404() throws Exception {
        when(clienteService.buscarPorId(99L)).thenReturn(null);
 
        mockMvc.perform(get("/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
 

    @Test
    void testActualizarCliente_exitoso_retorna200() throws Exception {
        Cliente clienteActualizado = new Cliente(1L, "Juan Modificado", "nuevo@correo.com", "newpass",
                new Rol(1L, "CLIENTE"));
        when(clienteService.actualizar(eq(1L), any(Cliente.class))).thenReturn(clienteActualizado);
 
        ClienteDTO dtoActualizado = new ClienteDTO(1L, "Juan Modificado", "nuevo@correo.com", "newpass", 1L);
 
        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Modificado"))
                .andExpect(jsonPath("$.correo").value("nuevo@correo.com"));
    }
 
    @Test
    void testActualizarCliente_noExiste_retorna404() throws Exception {
        when(clienteService.actualizar(eq(99L), any(Cliente.class)))
                .thenThrow(new RuntimeException("Cliente no encontrado con ID: 99"));
 
        mockMvc.perform(put("/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteDtoRequest)))
                .andExpect(status().isNotFound());
    }
     
    @Test
    void testEliminarCliente_exitoso_retorna204() throws Exception {
        doNothing().when(clienteService).eliminar(1L);
 
        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isNoContent());
 
        verify(clienteService, times(1)).eliminar(1L);
    }
 
    @Test
    void testEliminarCliente_noExiste_retorna404() throws Exception {
        doThrow(new RuntimeException("Cliente no encontrado con ID: 99"))
                .when(clienteService).eliminar(99L);
 
        mockMvc.perform(delete("/clientes/99"))
                .andExpect(status().isNotFound());
    }
}
