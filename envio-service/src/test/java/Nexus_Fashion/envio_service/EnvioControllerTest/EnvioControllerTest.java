package Nexus_Fashion.envio_service.EnvioControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import Nexus_Fashion.envio_service.controller.EnvioController;
import Nexus_Fashion.envio_service.dto.EnvioDTO;
import Nexus_Fashion.envio_service.exception.GlobalExceptionHandler;
import Nexus_Fashion.envio_service.model.Envio;
import Nexus_Fashion.envio_service.service.EnvioService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class EnvioControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EnvioService envioService;

    private Envio envioEjemplo;
    private EnvioDTO envioDtoRequest;

    @BeforeEach
    void setUp() {
        EnvioController controller = new EnvioController(envioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        envioEjemplo = Envio.builder()
                .idCompra(1L)
                .estadoEnvio("PENDIENTE")
                .fechaEnvio(LocalDateTime.now())
                .detalles(Collections.emptyList())
                .build();

        envioDtoRequest = new EnvioDTO(
                1L,
                "Av. Providencia 1234",
                "Providencia"
        );
    }
    @Test
    void testCrearEnvio_retorna200() throws Exception {
        when(envioService.guardar(any(Envio.class))).thenReturn(envioEjemplo);
 
        mockMvc.perform(post("/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(envioDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCompra").value(envioEjemplo.getIdCompra()))
                ;
    }
    
    @Test
    void listarEnvios_deberiaRetornarLista() throws Exception {
      
        when(envioService.obtenerTodos()).thenReturn(Arrays.asList(envioEjemplo));

       
        mockMvc.perform(get("/envios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(envioService, times(1)).obtenerTodos();
    }

    @Test
    void listarEnvios_listaVacia_deberiaRetornar200() throws Exception {
       
        when(envioService.obtenerTodos()).thenReturn(Collections.emptyList());

        
        mockMvc.perform(get("/envios")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(envioService, times(1)).obtenerTodos();
    }

    
    @Test
    void obtenerEnvioPorId_encontrado_deberiaRetornar200() throws Exception {
       
        when(envioService.buscarPorId(1L)).thenReturn(envioEjemplo);

     
        mockMvc.perform(get("/envios/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(envioService, times(1)).buscarPorId(1L);
    }

    
    @Test
    void obtenerEnvioPorId_noEncontrado_deberiaRetornar404() throws Exception {

        when(envioService.buscarPorId(99L)).thenReturn(null);

     
        mockMvc.perform(get("/envios/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(envioService, times(1)).buscarPorId(99L);
    }


    @Test
    void eliminarEnvio_existente_deberiaRetornar204() throws Exception {
   
        doNothing().when(envioService).eliminar(1L);

        mockMvc.perform(delete("/envios/1"))
                .andExpect(status().isNoContent());

        verify(envioService, times(1)).eliminar(1L);
    }

    
    @Test
    void eliminarEnvio_noExistente_deberiaRetornar404() throws Exception {
    
        doThrow(new RuntimeException("Envío no encontrado")).when(envioService).eliminar(99L);

     
        mockMvc.perform(delete("/envios/99"))
                .andExpect(status().isNotFound());

        verify(envioService, times(1)).eliminar(99L);
    }
}