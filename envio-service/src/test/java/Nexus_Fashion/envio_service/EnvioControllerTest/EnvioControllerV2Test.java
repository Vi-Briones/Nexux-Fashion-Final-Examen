package Nexus_Fashion.envio_service.EnvioControllerTest;

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

import Nexus_Fashion.envio_service.assemblers.EnvioModelAssembler;
import Nexus_Fashion.envio_service.controller.EnvioControllerV2;
import Nexus_Fashion.envio_service.dto.EnvioDTO;
import Nexus_Fashion.envio_service.exception.GlobalExceptionHandler;

import Nexus_Fashion.envio_service.model.Envio;
import Nexus_Fashion.envio_service.service.EnvioService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class EnvioControllerV2Test {

    private MockMvc mockMvc;

    @Mock
    private EnvioService envioService;

    @Mock
    private EnvioModelAssembler assembler;

    private Envio envioEjemplo;
    private EnvioDTO envioDtoEjemplo; 

    @BeforeEach
    void setUp() {
        EnvioControllerV2 controller = new EnvioControllerV2(envioService, assembler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        envioEjemplo = Envio.builder()
                .idCompra(1L)
                .estadoEnvio("PENDIENTE")
                .fechaEnvio(LocalDateTime.now())
                .build();

    
        envioDtoEjemplo = EnvioDTO.fromModel(envioEjemplo);
    }

  
    @Test
    void testListarEnvio_retornaListaHateoas() throws Exception {
    
        EntityModel<EnvioDTO> entityModel = EntityModel.of(envioDtoEjemplo); 
        when(envioService.obtenerTodos()).thenReturn(Arrays.asList(envioEjemplo));
        when(assembler.toModel(envioEjemplo)).thenReturn(entityModel); 
       
        mockMvc.perform(get("/v2/envios") 
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());

        verify(envioService, times(1)).obtenerTodos();
        verify(assembler, times(1)).toModel(envioEjemplo);
    }


    @Test
    void testListarEnvios_listaVacia_retorna200() throws Exception {
     
        when(envioService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v2/envios") 
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(envioService, times(1)).obtenerTodos();
        verify(assembler, never()).toModel(any());
    }

    @Test
    void testObtenerEnvio_encontrado_retorna200() throws Exception {
        
        EntityModel<EnvioDTO> entityModel = EntityModel.of(envioDtoEjemplo); 
        when(envioService.buscarPorId(1L)).thenReturn(envioEjemplo); 
        when(assembler.toModel(envioEjemplo)).thenReturn(entityModel);

       
        mockMvc.perform(get("/v2/envios/1") 
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(envioService, times(1)).buscarPorId(1L); 
        verify(assembler, times(1)).toModel(envioEjemplo);
    }

   
    @Test
    void testObtenerEnvio_noEncontrado_retorna404() throws Exception {
        
        when(envioService.buscarPorId(99L)).thenReturn(null); 

       
        mockMvc.perform(get("/v2/envios/99") 
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}