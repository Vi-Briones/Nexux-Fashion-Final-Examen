package com.Nexus_Fashion.cliente_service.ClienteControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.Nexus_Fashion.cliente_service.assemblers.ClienteModelAssembler;
import com.Nexus_Fashion.cliente_service.controller.ClienteControllerV2;
import com.Nexus_Fashion.cliente_service.dto.ClienteDTO;
import com.Nexus_Fashion.cliente_service.model.Cliente;
import com.Nexus_Fashion.cliente_service.model.Rol;
import com.Nexus_Fashion.cliente_service.service.ClienteService;

import java.util.Arrays;
import java.util.Collections;
 
@ExtendWith(MockitoExtension.class)
public class ClienteControllerV2Test {

    private MockMvc mockMvc;
 
    @Mock
    private ClienteService clienteService;
 
    @Mock
    private ClienteModelAssembler assembler;
 
    private Cliente clienteEjemplo;
    private ClienteDTO clienteDtoEjemplo;
 
    @BeforeEach
    void setUp() {
        ClienteControllerV2 controller = new ClienteControllerV2(clienteService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
 
        Rol rol = new Rol(1L, "CLIENTE");
        clienteEjemplo = new Cliente(1L, "Juan Pérez", "juan@correo.com", "password123", rol);
        clienteDtoEjemplo = new ClienteDTO(1L, "Juan Pérez", "juan@correo.com", null, 1L);
    }

    @Test
    void testListarClientes_retornaListaHateoas() throws Exception {
        EntityModel<ClienteDTO> entityModel = EntityModel.of(clienteDtoEjemplo);
        when(clienteService.listar()).thenReturn(Arrays.asList(clienteEjemplo));
        when(assembler.toModel(clienteEjemplo)).thenReturn(entityModel);
 
        mockMvc.perform(get("/v2/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(clienteService, times(1)).listar();
        verify(assembler, times(1)).toModel(clienteEjemplo);
    }
 
    @Test
    void testListarClientes_listaVacia_retorna200() throws Exception {
        when(clienteService.listar()).thenReturn(Collections.emptyList());
 
        mockMvc.perform(get("/v2/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(clienteService, times(1)).listar();
        verify(assembler, never()).toModel(any());
    }

    @Test
    void testObtenerCliente_encontrado_retorna200() throws Exception {
        EntityModel<ClienteDTO> entityModel = EntityModel.of(clienteDtoEjemplo);
        when(clienteService.buscarPorId(1L)).thenReturn(clienteEjemplo);
        when(assembler.toModel(clienteEjemplo)).thenReturn(entityModel);
 
        mockMvc.perform(get("/v2/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
 
        verify(clienteService, times(1)).buscarPorId(1L);
        verify(assembler, times(1)).toModel(clienteEjemplo);
    }
 
    @Test
    void testObtenerCliente_noEncontrado_retorna404() throws Exception {
        when(clienteService.buscarPorId(99L)).thenReturn(null);
 
        mockMvc.perform(get("/v2/clientes/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
 
        verify(assembler, never()).toModel(any());
    }
}
