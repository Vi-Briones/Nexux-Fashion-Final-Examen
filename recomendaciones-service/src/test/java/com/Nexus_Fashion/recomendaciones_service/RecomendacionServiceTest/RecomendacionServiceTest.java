package com.Nexus_Fashion.recomendaciones_service.RecomendacionServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.Nexus_Fashion.recomendaciones_service.dto.RecomendacionDTO;
import com.Nexus_Fashion.recomendaciones_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;
import com.Nexus_Fashion.recomendaciones_service.repository.RecomendacionRepository;
import com.Nexus_Fashion.recomendaciones_service.service.RecomendacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RecomendacionServiceTest {

    @Mock
    private RecomendacionRepository recomendacionRepository;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private RecomendacionService recomendacionService;

    private Recomendacion recomendacionEjemplo;

    @BeforeEach
    void setUp() {
        recomendacionEjemplo = new Recomendacion(
                1L, 5L, 10L,
                "PRODUCTO_SIMILAR",
                "Te recomendamos este producto basado en tus compras anteriores",
                85.0
        );
    }
    
    // ─────────────────────────── listar ────────────────────────────

    @Test
    void testListar_retornaLista() {
        when(recomendacionRepository.findAll()).thenReturn(Arrays.asList(recomendacionEjemplo));

        List<Recomendacion> resultado = recomendacionService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        verify(recomendacionRepository, times(1)).findAll();
    }

    @Test
    void testListar_listaVacia() {
        when(recomendacionRepository.findAll()).thenReturn(Collections.emptyList());

        List<Recomendacion> resultado = recomendacionService.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(recomendacionRepository, times(1)).findAll();
    }

    // ─────────────────────────── buscarPorId ───────────────────────

    @Test
    void testBuscarPorId_encontrado() {
        when(recomendacionRepository.findById(1L)).thenReturn(Optional.of(recomendacionEjemplo));

        Recomendacion resultado = recomendacionService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(recomendacionRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_noEncontrado_retornaNull() {
        when(recomendacionRepository.findById(99L)).thenReturn(Optional.empty());

        Recomendacion resultado = recomendacionService.buscarPorId(99L);

        assertNull(resultado);
        verify(recomendacionRepository, times(1)).findById(99L);
    }

    // ─────────────────────────── existePorId ───────────────────────

    @Test
    void testExistePorId_existe_retornaTrue() {
        when(recomendacionRepository.existsById(1L)).thenReturn(true);

        boolean resultado = recomendacionService.existePorId(1L);

        assertTrue(resultado);
        verify(recomendacionRepository, times(1)).existsById(1L);
    }

    @Test
    void testExistePorId_noExiste_retornaFalse() {
        when(recomendacionRepository.existsById(99L)).thenReturn(false);

        boolean resultado = recomendacionService.existePorId(99L);

        assertFalse(resultado);
        verify(recomendacionRepository, times(1)).existsById(99L);
    }

    // ──────────────────── listarPorCliente ─────────────────────────

    @Test
    void testListarPorCliente_retornaLista() {
        when(recomendacionRepository.findByIdCliente(5L)).thenReturn(Arrays.asList(recomendacionEjemplo));

        List<Recomendacion> resultado = recomendacionService.listarPorCliente(5L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getIdCliente());
        verify(recomendacionRepository, times(1)).findByIdCliente(5L);
    }

    @Test
    void testListarPorCliente_sinResultados_retornaListaVacia() {
        when(recomendacionRepository.findByIdCliente(99L)).thenReturn(Collections.emptyList());

        List<Recomendacion> resultado = recomendacionService.listarPorCliente(99L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(recomendacionRepository, times(1)).findByIdCliente(99L);
    }

    // ─────────────────────────── actualizar ────────────────────────

    @Test
    void testActualizar_exitoso() {
        Recomendacion datosActualizados = new Recomendacion(
                null, 5L, 10L,
                "TENDENCIA",
                "Producto en tendencia esta temporada",
                90.0
        );
        Recomendacion actualizada = new Recomendacion(
                1L, 5L, 10L,
                "TENDENCIA",
                "Producto en tendencia esta temporada",
                90.0
        );

        when(recomendacionRepository.findById(1L)).thenReturn(Optional.of(recomendacionEjemplo));
        when(recomendacionRepository.save(any(Recomendacion.class))).thenReturn(actualizada);

        Recomendacion resultado = recomendacionService.actualizar(1L, datosActualizados);

        assertNotNull(resultado);
        assertEquals("TENDENCIA", resultado.getTipoRecomendacion());
        assertEquals(90.0, resultado.getPuntajeAfinidad());
        verify(recomendacionRepository, times(1)).findById(1L);
        verify(recomendacionRepository, times(1)).save(any(Recomendacion.class));
    }

    @Test
    void testActualizar_noExiste_lanzaExcepcion() {
        when(recomendacionRepository.findById(99L)).thenReturn(Optional.empty());

        Recomendacion datos = new Recomendacion(null, 5L, 10L, "TENDENCIA", "Comentario", 90.0);

        assertThrows(ResourceNotFoundException.class,
                () -> recomendacionService.actualizar(99L, datos));

        verify(recomendacionRepository, never()).save(any());
    }

    // ─────────────────────────── eliminar ──────────────────────────

    @Test
    void testEliminar_exitoso() {
        when(recomendacionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(recomendacionRepository).deleteById(1L);

        recomendacionService.eliminar(1L);

        verify(recomendacionRepository, times(1)).existsById(1L);
        verify(recomendacionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminar_noExiste_lanzaExcepcion() {
        when(recomendacionRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> recomendacionService.eliminar(99L));

        verify(recomendacionRepository, never()).deleteById(any());
    }

    // ──────────────── puntajeAfinidad (reglas de negocio) ──────────

    @Test
    void testPuntajeAfinidad_enRangoValido_aceptado() {
        Recomendacion rec = new Recomendacion(null, 1L, 1L, "TIPO", "Comentario", 50.0);
        assertTrue(rec.getPuntajeAfinidad() >= 0 && rec.getPuntajeAfinidad() <= 100);
    }

    @Test
    void testPuntajeAfinidad_limiteMinimo_esValido() {
        Recomendacion rec = new Recomendacion(null, 1L, 1L, "TIPO", "Comentario", 0.0);
        assertEquals(0.0, rec.getPuntajeAfinidad());
    }

    @Test
    void testPuntajeAfinidad_limiteMaximo_esValido() {
        Recomendacion rec = new Recomendacion(null, 1L, 1L, "TIPO", "Comentario", 100.0);
        assertEquals(100.0, rec.getPuntajeAfinidad());
    }
}