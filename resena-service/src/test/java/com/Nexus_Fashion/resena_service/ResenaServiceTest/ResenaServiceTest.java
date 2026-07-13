package com.Nexus_Fashion.resena_service.ResenaServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.Nexus_Fashion.resena_service.dto.ResenaDTO;
import com.Nexus_Fashion.resena_service.model.Resena;
import com.Nexus_Fashion.resena_service.repository.ResenaRepository;
import com.Nexus_Fashion.resena_service.service.ResenaService;

@ExtendWith(MockitoExtension.class)
public class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ResenaService resenaService;

    private Resena resenaEjemplo;
    private ResenaDTO resenaDTOEjemplo;

    @BeforeEach
    void setUp() {
        // El campo @Value no se inyecta con @InjectMocks, hay que forzarlo manualmente
        ReflectionTestUtils.setField(resenaService, "compraPath", "http://api-gateway/compras/%d/existe");

        resenaEjemplo = new Resena(1L, 100L, "Juan Perez", 5, "Excelente producto");

        resenaDTOEjemplo = new ResenaDTO(null, 100L, "Juan Perez", 5, "Excelente producto");
    }

    // Helper para mockear la cadena completa del WebClient
    @SuppressWarnings("unchecked")
    private void mockWebClientRespuesta(Boolean valorRetornado) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(valorRetornado));
    }

    private void mockWebClientLanzaExcepcion() {
        when(webClient.get()).thenThrow(new RuntimeException("Connection refused"));
    }

    // ---------- guardar ----------

    @Test
    void testGuardar_compraValida_exitoso() {
        mockWebClientRespuesta(true);
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaEjemplo);

        ResenaDTO resultado = resenaService.guardar(resenaDTOEjemplo);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Perez", resultado.getCliente());
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    void testGuardar_fallaConexionServicioExterno_usaFallbackYGuardaIgual() {
        // El servicio de compras falla, pero el catch interno hace fallback y guarda igual
        mockWebClientLanzaExcepcion();
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaEjemplo);

        ResenaDTO resultado = resenaService.guardar(resenaDTOEjemplo);

        assertNotNull(resultado);
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    void testGuardar_repositorioLanzaExcepcion_lanzaRuntimeException() {
        mockWebClientRespuesta(true);
        when(resenaRepository.save(any(Resena.class)))
                .thenThrow(new RuntimeException("Error de conexión a BD"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.guardar(resenaDTOEjemplo));

        assertTrue(ex.getMessage().contains("Fallo en POST"));
    }

    // ---------- actualizar ----------

    @Test
    void testActualizar_exitoso() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaEjemplo));
        mockWebClientRespuesta(true);
        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaEjemplo);

        ResenaDTO dtoActualizado = new ResenaDTO(null, 100L, "Juan Perez", 4, "Bueno, pero podría mejorar");

        ResenaDTO resultado = resenaService.actualizar(1L, dtoActualizado);

        assertNotNull(resultado);
        verify(resenaRepository, times(1)).save(resenaEjemplo);
    }

    @Test
    void testActualizar_noExiste_lanzaExcepcion() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.actualizar(99L, resenaDTOEjemplo));

        assertTrue(ex.getMessage().contains("no existe en la base de datos"));
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void testActualizar_compraNoExisteEnServicioExterno_lanzaExcepcion() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaEjemplo));
        mockWebClientRespuesta(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.actualizar(1L, resenaDTOEjemplo));

        assertTrue(ex.getMessage().contains("Fallo en PUT"));
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    // ---------- listar ----------

    @Test
    void testListar_retornaLista() {
        when(resenaRepository.findAll()).thenReturn(Arrays.asList(resenaEjemplo));

        List<ResenaDTO> resultado = resenaService.listar();

        assertEquals(1, resultado.size());
        verify(resenaRepository, times(1)).findAll();
    }

    @Test
    void testListar_listaVacia() {
        when(resenaRepository.findAll()).thenReturn(Collections.emptyList());

        List<ResenaDTO> resultado = resenaService.listar();

        assertTrue(resultado.isEmpty());
    }

    // ---------- obtenerPorId ----------

    @Test
    void testObtenerPorId_encontrado() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaEjemplo));

        ResenaDTO resultado = resenaService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testObtenerPorId_noEncontrado_lanzaExcepcion() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> resenaService.obtenerPorId(99L));

        assertEquals("Reseña no encontrada con ID: 99", ex.getMessage());
    }

    // ---------- eliminar ----------

    @Test
    void testEliminar_exitoso() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaEjemplo));
        doNothing().when(resenaRepository).delete(resenaEjemplo);

        resenaService.eliminar(1L);

        verify(resenaRepository, times(1)).delete(resenaEjemplo);
    }

    @Test
    void testEliminar_noExiste_lanzaExcepcion() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resenaService.eliminar(99L));

        verify(resenaRepository, never()).delete(any(Resena.class));
    }
}
