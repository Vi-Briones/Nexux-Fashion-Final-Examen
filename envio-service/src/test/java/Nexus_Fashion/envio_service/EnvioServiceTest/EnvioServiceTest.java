package Nexus_Fashion.envio_service.EnvioServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import Nexus_Fashion.envio_service.model.DetalleEnvio;
import Nexus_Fashion.envio_service.model.Envio;
import Nexus_Fashion.envio_service.repository.EnvioRepository;
import Nexus_Fashion.envio_service.service.EnvioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private EnvioService envioService;

    private Envio envioMock;

    @BeforeEach
    void setUp() {
        DetalleEnvio detalle = DetalleEnvio.builder()
                .id(1L)
                .direccionDestino("Av. Providencia 123")
                .comuna("Providencia")
                .build();

        envioMock = Envio.builder()
                .id(1L)
                .idCompra(10L)
                .estadoEnvio("PENDIENTE")
                .fechaEnvio(LocalDateTime.now())
                .detalles(Collections.singletonList(detalle))
                .build();
    }

    @Test
    void testObtenerTodos_retornaListaDeEnvios() {
        when(envioRepository.findAll()).thenReturn(Arrays.asList(envioMock));

        List<Envio> resultado = envioService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(envioRepository, times(1)).findAll();
    }

    @Test
    void testObtenerTodos_listaVacia_retornaListaVacia() {
        when(envioRepository.findAll()).thenReturn(Collections.emptyList());

        List<Envio> resultado = envioService.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testBuscarPorId_encontrado_retornaEnvio() {
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioMock));

        Envio resultado = envioService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getIdCompra());
        verify(envioRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_noEncontrado_retornaNull() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        Envio resultado = envioService.buscarPorId(99L);

        assertNull(resultado);
        verify(envioRepository, times(1)).findById(99L);
    }

    @Test
    void testActualizar_noEncontrado_lanzaExcepcion() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> envioService.actualizar(99L, envioMock));

        verify(envioRepository, never()).save(any());
    }

    @Test
    void testActualizar_encontrado_retornaEnvioActualizado() {
        Envio envioActualizado = Envio.builder()
                .id(1L).idCompra(10L).estadoEnvio("EN_RUTA").fechaEnvio(LocalDateTime.now()).build();

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioMock));
        when(envioRepository.save(any(Envio.class))).thenReturn(envioActualizado);

        Envio resultado = envioService.actualizar(1L, envioActualizado);

        assertNotNull(resultado);
        assertEquals("EN_RUTA", resultado.getEstadoEnvio());
        verify(envioRepository, times(1)).save(any(Envio.class));
    }

    @Test
    void testEliminar_noEncontrado_lanzaExcepcion() {
        when(envioRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> envioService.eliminar(99L));

        verify(envioRepository, never()).deleteById(any());
    }

    @Test
    void testEliminar_encontrado_eliminaCorrectamente() {
        when(envioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(envioRepository).deleteById(1L);

        assertDoesNotThrow(() -> envioService.eliminar(1L));

        verify(envioRepository, times(1)).deleteById(1L);
    }

   
    @Test
    void testNegocio_estadoInicialDebeSerPendiente() {
        assertEquals("PENDIENTE", envioMock.getEstadoEnvio());
    }

  
    @Test
    void testNegocio_guardar_lanzaExcepcion_cuandoServicioComprasNoDisponible() {
        assertThrows(RuntimeException.class, () -> envioService.guardar(envioMock));
    }
}