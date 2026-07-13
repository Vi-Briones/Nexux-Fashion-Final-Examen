package com.Nexus_Fashion.notificacion.InventarioServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.Nexus_Fashion.notificacion_service.model.Notificacion;
import com.Nexus_Fashion.notificacion_service.repository.NotificacionRepository;
import com.Nexus_Fashion.notificacion_service.service.NotificacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Notificacion notificacionEjemplo;

    @BeforeEach
    void setUp() {
        notificacionEjemplo = Notificacion.builder()
                .id(1L)
                .idUsuario(1L)
                .tipoEvento("DESPACHO_EN_CAMINO")
                .mensaje("Nexus Fashion: tu pedido está en camino")
                .leido(false)
                .fechaEnvio(LocalDateTime.now())
                .build();
    }

    // ─────────────────────────── listar ────────────────────────────

    @Test
    void testListar_retornaLista() {
        when(notificacionRepository.findAll()).thenReturn(Arrays.asList(notificacionEjemplo));

        List<Notificacion> resultado = notificacionService.listar();

        assertEquals(1, resultado.size());
        verify(notificacionRepository, times(1)).findAll();
    }

    @Test
    void testListar_listaVacia() {
        when(notificacionRepository.findAll()).thenReturn(Collections.emptyList());

        List<Notificacion> resultado = notificacionService.listar();

        assertTrue(resultado.isEmpty());
        verify(notificacionRepository, times(1)).findAll();
    }

    // ─────────────────────────── obtenerPorId ─────────────────────

    @Test
    void testObtenerPorId_encontrado() {
        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionEjemplo));

        Notificacion resultado = notificacionService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(notificacionRepository, times(1)).findById(1L);
    }

    @Test
    void testObtenerPorId_noEncontrado_lanzaExcepcion() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificacionService.obtenerPorId(99L));

        assertEquals("Notificación no existe", ex.getMessage());
        verify(notificacionRepository, times(1)).findById(99L);
    }

    // ─────────────────────────── guardar ───────────────────────────

    @Test
    void testGuardar_exitoso() {
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacionEjemplo);

        Notificacion resultado = notificacionService.guardar(notificacionEjemplo);

        assertNotNull(resultado);
        assertEquals("DESPACHO_EN_CAMINO", resultado.getTipoEvento());
        assertEquals(1L, resultado.getIdUsuario());
        verify(notificacionRepository, times(1)).save(notificacionEjemplo);
    }

    @Test
    void testGuardar_sinFechaEnvio_asignaFechaAutomatica() {
        Notificacion sinFecha = Notificacion.builder()
                .idUsuario(1L)
                .tipoEvento("PEDIDO_CONFIRMADO")
                .mensaje("Tu pedido fue confirmado")
                .leido(false)
                .fechaEnvio(null)
                .build();
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(sinFecha);

        Notificacion resultado = notificacionService.guardar(sinFecha);

        assertNotNull(resultado);
        // El servicio asigna LocalDateTime.now() si fechaEnvio es null
        assertNotNull(sinFecha.getFechaEnvio());
        verify(notificacionRepository, times(1)).save(sinFecha);
    }

    @Test
    void testGuardar_idUsuarioNulo_lanzaExcepcion() {
        Notificacion invalida = Notificacion.builder()
                .idUsuario(null)
                .tipoEvento("PEDIDO_CONFIRMADO")
                .mensaje("Mensaje válido")
                .leido(false)
                .fechaEnvio(LocalDateTime.now())
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.guardar(invalida));

        assertEquals("idUsuario requerido y debe ser mayor a 0", ex.getMessage());
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    void testGuardar_idUsuarioCero_lanzaExcepcion() {
        Notificacion invalida = Notificacion.builder()
                .idUsuario(0L)
                .tipoEvento("PEDIDO_CONFIRMADO")
                .mensaje("Mensaje válido")
                .leido(false)
                .fechaEnvio(LocalDateTime.now())
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.guardar(invalida));

        assertEquals("idUsuario requerido y debe ser mayor a 0", ex.getMessage());
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    void testGuardar_tipoEventoVacio_lanzaExcepcion() {
        Notificacion invalida = Notificacion.builder()
                .idUsuario(1L)
                .tipoEvento("   ")
                .mensaje("Mensaje válido")
                .leido(false)
                .fechaEnvio(LocalDateTime.now())
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.guardar(invalida));

        assertEquals("tipoEvento requerido", ex.getMessage());
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    void testGuardar_mensajeVacio_lanzaExcepcion() {
        Notificacion invalida = Notificacion.builder()
                .idUsuario(1L)
                .tipoEvento("PEDIDO_CONFIRMADO")
                .mensaje("")
                .leido(false)
                .fechaEnvio(LocalDateTime.now())
                .build();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.guardar(invalida));

        assertEquals("mensaje requerido", ex.getMessage());
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    // ─────────────────────────── actualizar ────────────────────────

    @Test
    void testActualizar_exitoso() {
        Notificacion actualizada = Notificacion.builder()
                .id(1L)
                .idUsuario(2L)
                .tipoEvento("PEDIDO_ENTREGADO")
                .mensaje("Tu pedido fue entregado")
                .leido(true)
                .fechaEnvio(LocalDateTime.now())
                .build();

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionEjemplo));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(actualizada);

        Notificacion resultado = notificacionService.actualizar(1L, actualizada);

        assertNotNull(resultado);
        assertEquals("PEDIDO_ENTREGADO", resultado.getTipoEvento());
        assertEquals(2L, resultado.getIdUsuario());
        verify(notificacionRepository, times(1)).findById(1L);
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void testActualizar_noExiste_lanzaExcepcion() {
        when(notificacionRepository.findById(99L)).thenReturn(Optional.empty());

        Notificacion datos = Notificacion.builder()
                .idUsuario(1L)
                .tipoEvento("PEDIDO_ENTREGADO")
                .mensaje("Mensaje")
                .build();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificacionService.actualizar(99L, datos));

        assertEquals("Notificación no existe", ex.getMessage());
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    void testActualizar_idUsuarioInvalido_lanzaExcepcion() {
        Notificacion datosInvalidos = Notificacion.builder()
                .idUsuario(0L)
                .tipoEvento("PEDIDO_ENTREGADO")
                .mensaje("Mensaje válido")
                .build();

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionEjemplo));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.actualizar(1L, datosInvalidos));

        assertEquals("idUsuario requerido", ex.getMessage());
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    void testActualizar_tipoEventoVacio_lanzaExcepcion() {
        Notificacion datosInvalidos = Notificacion.builder()
                .idUsuario(1L)
                .tipoEvento("")
                .mensaje("Mensaje válido")
                .build();

        when(notificacionRepository.findById(1L)).thenReturn(Optional.of(notificacionEjemplo));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.actualizar(1L, datosInvalidos));

        assertEquals("tipoEvento requerido", ex.getMessage());
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    void testEliminar_exitoso() {
        when(notificacionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(notificacionRepository).deleteById(1L);

        notificacionService.eliminar(1L);

        verify(notificacionRepository, times(1)).existsById(1L);
        verify(notificacionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminar_noExiste_lanzaExcepcion() {
        when(notificacionRepository.existsById(99L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificacionService.eliminar(99L));

        assertEquals("Notificación no existe", ex.getMessage());
        verify(notificacionRepository, never()).deleteById(any());
    }
}