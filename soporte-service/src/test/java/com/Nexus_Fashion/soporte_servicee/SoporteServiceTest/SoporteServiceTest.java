package com.Nexus_Fashion.soporte_servicee.SoporteServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.Nexus_Fashion.soporte_service.model.Soporte;
import com.Nexus_Fashion.soporte_service.repository.SoporteRepository;
import com.Nexus_Fashion.soporte_service.service.SoporteService;

@ExtendWith(MockitoExtension.class)
public class SoporteServiceTest {

    @Mock
    private SoporteRepository soporteRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private SoporteService soporteService;

    private Soporte soporteEjemplo;
    private Soporte soporteValidoNuevo;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(soporteService, "clientePath", "http://api-gateway/clientes/%d/existe");

        soporteEjemplo = new Soporte(1L, 10L, "Problema con el login", "No puedo ingresar a mi cuenta",
                "PENDIENTE", "ALTA", LocalDateTime.now(), null);

        soporteValidoNuevo = new Soporte(null, 10L, "Problema con el login", "No puedo ingresar a mi cuenta",
                null, null, null, null);
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientRespuesta(Boolean valorRetornado) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.justOrEmpty(valorRetornado)); // ← cambio aquí
}

    private void mockWebClientLanzaExcepcion() {
        when(webClient.get()).thenThrow(new RuntimeException("Connection refused"));
    }

    // ---------- guardar: validaciones de entrada ----------

    @Test
    void testGuardar_idClienteNull_lanzaExcepcion() {
        Soporte invalido = new Soporte(null, null, "Asunto", "Descripcion", null, null, null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> soporteService.guardar(invalido));

        assertEquals("idCliente (Cliente) requerido y debe ser mayor a 0", ex.getMessage());
        verify(soporteRepository, never()).save(any());
    }

    @Test
    void testGuardar_idClienteMenorOIgualCero_lanzaExcepcion() {
        Soporte invalido = new Soporte(null, 0L, "Asunto", "Descripcion", null, null, null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> soporteService.guardar(invalido));

        assertEquals("idCliente (Cliente) requerido y debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void testGuardar_asuntoVacio_lanzaExcepcion() {
        Soporte invalido = new Soporte(null, 10L, "  ", "Descripcion", null, null, null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> soporteService.guardar(invalido));

        assertEquals("asunto requerido", ex.getMessage());
    }

    @Test
    void testGuardar_descripcionVacia_lanzaExcepcion() {
        Soporte invalido = new Soporte(null, 10L, "Asunto", "", null, null, null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> soporteService.guardar(invalido));

        assertEquals("descripción requerida", ex.getMessage());
    }

    // ---------- guardar: valores por defecto ----------

    @Test
    void testGuardar_sinEstadoNiPrioridad_asignaValoresPorDefecto() {
        mockWebClientRespuesta(true);
        when(soporteRepository.save(any(Soporte.class))).thenAnswer(inv -> inv.getArgument(0));

        Soporte resultado = soporteService.guardar(soporteValidoNuevo);

        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("MEDIA", resultado.getPrioridad());
        assertNotNull(resultado.getFechaCreacion());
        verify(soporteRepository, times(1)).save(any(Soporte.class));
    }

    @Test
    void testGuardar_conEstadoYPrioridadDefinidos_respetaValores() {
        mockWebClientRespuesta(true);
        when(soporteRepository.save(any(Soporte.class))).thenReturn(soporteEjemplo);

        Soporte resultado = soporteService.guardar(soporteEjemplo);

        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("ALTA", resultado.getPrioridad());
    }

    // ---------- guardar: validación externa de cliente ----------

    @Test
    void testGuardar_clienteValido_exitoso() {
        mockWebClientRespuesta(true);
        when(soporteRepository.save(any(Soporte.class))).thenReturn(soporteEjemplo);

        Soporte resultado = soporteService.guardar(soporteValidoNuevo);

        assertNotNull(resultado);
        verify(soporteRepository, times(1)).save(any(Soporte.class));
    }

    @Test
    void testGuardar_fallaConexionWebClient_usaFallbackYGuardaIgual() {
        mockWebClientLanzaExcepcion();
        when(soporteRepository.save(any(Soporte.class))).thenReturn(soporteEjemplo);

        Soporte resultado = soporteService.guardar(soporteValidoNuevo);

        assertNotNull(resultado);
        verify(soporteRepository, times(1)).save(any(Soporte.class));
    }

    @Test
    void testGuardar_respuestaClienteNull_lanzaExcepcion() {
        mockWebClientRespuesta(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.guardar(soporteValidoNuevo));

        assertEquals("No se pudo validar la existencia del cliente", ex.getMessage());
        verify(soporteRepository, never()).save(any());
    }

    @Test
    void testGuardar_clienteNoExiste_lanzaExcepcion() {
        mockWebClientRespuesta(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.guardar(soporteValidoNuevo));

        assertEquals("Cliente no existe", ex.getMessage());
        verify(soporteRepository, never()).save(any());
    }

    // ---------- listar ----------

    @Test
    void testListar_retornaLista() {
        when(soporteRepository.findAll()).thenReturn(Arrays.asList(soporteEjemplo));

        List<Soporte> resultado = soporteService.listar();

        assertEquals(1, resultado.size());
    }

    @Test
    void testListar_listaVacia() {
        when(soporteRepository.findAll()).thenReturn(Collections.emptyList());

        List<Soporte> resultado = soporteService.listar();

        assertTrue(resultado.isEmpty());
    }

    // ---------- obtenerPorId ----------

    @Test
    void testObtenerPorId_encontrado() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporteEjemplo));

        Soporte resultado = soporteService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testObtenerPorId_noEncontrado_lanzaExcepcion() {
        when(soporteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.obtenerPorId(99L));

        assertEquals("Ticket de soporte no existe", ex.getMessage());
    }

    // ---------- actualizar ----------

    @Test
    void testActualizar_exitoso() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporteEjemplo));
        mockWebClientRespuesta(true);
        when(soporteRepository.save(any(Soporte.class))).thenReturn(soporteEjemplo);

        Soporte cambios = new Soporte(null, 10L, "Asunto actualizado", "Descripcion actualizada",
                "EN_PROCESO", "MEDIA", null, null);

        Soporte resultado = soporteService.actualizar(1L, cambios);

        assertNotNull(resultado);
        verify(soporteRepository, times(1)).save(soporteEjemplo);
    }

    @Test
    void testActualizar_noExiste_lanzaExcepcion() {
        when(soporteRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.actualizar(99L, soporteEjemplo));

        assertEquals("Ticket de soporte no existe", ex.getMessage());
        verify(soporteRepository, never()).save(any());
    }

    @Test
    void testActualizar_asuntoVacio_lanzaExcepcion() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporteEjemplo));

        Soporte cambios = new Soporte(null, 10L, "", "Descripcion", "EN_PROCESO", "MEDIA", null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> soporteService.actualizar(1L, cambios));

        assertEquals("asunto requerido", ex.getMessage());
    }

    @Test
    void testActualizar_descripcionVacia_lanzaExcepcion() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporteEjemplo));

        Soporte cambios = new Soporte(null, 10L, "Asunto", "  ", "EN_PROCESO", "MEDIA", null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> soporteService.actualizar(1L, cambios));

        assertEquals("descripción requerida", ex.getMessage());
    }

    @Test
    void testActualizar_estadoVacio_lanzaExcepcion() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporteEjemplo));

        Soporte cambios = new Soporte(null, 10L, "Asunto", "Descripcion", "", "MEDIA", null, null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> soporteService.actualizar(1L, cambios));

        assertEquals("estado requerido", ex.getMessage());
    }

    @Test
    void testActualizar_respuestaClienteNull_lanzaExcepcion() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporteEjemplo));
        mockWebClientRespuesta(null);

        Soporte cambios = new Soporte(null, 10L, "Asunto", "Descripcion", "EN_PROCESO", "MEDIA", null, null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.actualizar(1L, cambios));

        assertEquals("No se pudo validar la existencia del cliente", ex.getMessage());
        verify(soporteRepository, never()).save(any());
    }

    @Test
    void testActualizar_clienteNoExiste_lanzaExcepcion() {
        when(soporteRepository.findById(1L)).thenReturn(Optional.of(soporteEjemplo));
        mockWebClientRespuesta(false);

        Soporte cambios = new Soporte(null, 10L, "Asunto", "Descripcion", "EN_PROCESO", "MEDIA", null, null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.actualizar(1L, cambios));

        assertEquals("Cliente no existe", ex.getMessage());
        verify(soporteRepository, never()).save(any());
    }

    // ---------- eliminar ----------

    @Test
    void testEliminar_exitoso() {
        when(soporteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(soporteRepository).deleteById(1L);

        soporteService.eliminar(1L);

        verify(soporteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminar_noExiste_lanzaExcepcion() {
        when(soporteRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> soporteService.eliminar(99L));

        assertEquals("Ticket de soporte no existe", ex.getMessage());
        verify(soporteRepository, never()).deleteById(any());
    }
}
