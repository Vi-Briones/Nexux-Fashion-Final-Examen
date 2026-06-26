package com.Nexus_Fashion.compra_service.CompraServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import com.Nexus_Fashion.compra_service.model.Compra;
import com.Nexus_Fashion.compra_service.model.DetalleCompra;
import com.Nexus_Fashion.compra_service.repository.CompraRepository;
import com.Nexus_Fashion.compra_service.service.CompraService;

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
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private CompraService compraService;

    private Compra compraEjemplo;
    private DetalleCompra detalleEjemplo;

    @BeforeEach
    void setUp() {
        detalleEjemplo = new DetalleCompra(null, 10L, 2, 25000.0);

        compraEjemplo = new Compra(
                1L,
                5L,
                50000.0,
                Collections.singletonList(detalleEjemplo)
        );
    }

    // ─────────────────────────── guardar ───────────────────────────

    @Test
    void testGuardar_exitoso() {
        // El servicio tiene un try/catch que absorbe errores del WebClient,
        // por lo que aunque el cliente externo falle, la compra se guarda igual.
        when(compraRepository.save(any(Compra.class))).thenReturn(compraEjemplo);

        Compra resultado = compraService.guardar(compraEjemplo);

        assertNotNull(resultado);
        assertEquals(50000.0, resultado.getTotal());
        assertEquals(1L, resultado.getId());
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    // ─────────────────────────── listar ────────────────────────────

    @Test
    void testListar_retornaLista() {
        when(compraRepository.findAll()).thenReturn(Arrays.asList(compraEjemplo));

        List<Compra> resultado = compraService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        verify(compraRepository, times(1)).findAll();
    }

    @Test
    void testListar_listaVacia() {
        when(compraRepository.findAll()).thenReturn(Collections.emptyList());

        List<Compra> resultado = compraService.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(compraRepository, times(1)).findAll();
    }

    // ─────────────────────────── buscarPorId ───────────────────────

    @Test
    void testBuscarPorId_encontrado() {
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraEjemplo));

        Compra resultado = compraService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(compraRepository, times(1)).findById(1L);
    }

    @Test
    void testBuscarPorId_noEncontrado_retornaNull() {
        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        Compra resultado = compraService.buscarPorId(99L);

        assertNull(resultado);
        verify(compraRepository, times(1)).findById(99L);
    }

    // ─────────────────────────── existePorId ───────────────────────

    @Test
    void testExistePorId_existe_retornaTrue() {
        when(compraRepository.existsById(1L)).thenReturn(true);

        boolean resultado = compraService.existePorId(1L);

        assertTrue(resultado);
        verify(compraRepository, times(1)).existsById(1L);
    }

    @Test
    void testExistePorId_noExiste_retornaFalse() {
        when(compraRepository.existsById(99L)).thenReturn(false);

        boolean resultado = compraService.existePorId(99L);

        assertFalse(resultado);
        verify(compraRepository, times(1)).existsById(99L);
    }

    // ─────────────────────── listarPorCliente ──────────────────────

    @Test
    void testListarPorCliente_retornaLista() {
        when(compraRepository.findByIdCliente(5L)).thenReturn(Arrays.asList(compraEjemplo));

        List<Compra> resultado = compraService.listarPorCliente(5L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getIdCliente());
        verify(compraRepository, times(1)).findByIdCliente(5L);
    }

    @Test
    void testListarPorCliente_sinCompras_retornaListaVacia() {
        when(compraRepository.findByIdCliente(99L)).thenReturn(Collections.emptyList());

        List<Compra> resultado = compraService.listarPorCliente(99L);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(compraRepository, times(1)).findByIdCliente(99L);
    }

    // ──────────────────── totalComprasPorCliente ───────────────────

    @Test
    void testTotalComprasPorCliente_retornaCantidad() {
        when(compraRepository.countByIdCliente(5L)).thenReturn(3L);

        long resultado = compraService.totalComprasPorCliente(5L);

        assertEquals(3L, resultado);
        verify(compraRepository, times(1)).countByIdCliente(5L);
    }

    @Test
    void testTotalComprasPorCliente_sinCompras_retornaCero() {
        when(compraRepository.countByIdCliente(99L)).thenReturn(0L);

        long resultado = compraService.totalComprasPorCliente(99L);

        assertEquals(0L, resultado);
        verify(compraRepository, times(1)).countByIdCliente(99L);
    }

    // ─────────────────────────── actualizar ────────────────────────

    @Test
    void testActualizar_exitoso() {
        Compra compraActualizada = new Compra(1L, 5L, 75000.0, Collections.emptyList());

        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraEjemplo));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraActualizada);

        Compra resultado = compraService.actualizar(1L, compraActualizada);

        assertNotNull(resultado);
        assertEquals(75000.0, resultado.getTotal());
        verify(compraRepository, times(1)).findById(1L);
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    void testActualizar_noExiste_lanzaExcepcion() {
        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        Compra compraActualizada = new Compra(99L, 5L, 75000.0, Collections.emptyList());

        assertThrows(RuntimeException.class,
                () -> compraService.actualizar(99L, compraActualizada));

        verify(compraRepository, never()).save(any());
    }

    // ─────────────────────────── eliminar ──────────────────────────

    @Test
    void testEliminar_exitoso() {
        when(compraRepository.existsById(1L)).thenReturn(true);
        doNothing().when(compraRepository).deleteById(1L);

        compraService.eliminar(1L);

        verify(compraRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminar_noExiste_lanzaExcepcion() {
        when(compraRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> compraService.eliminar(99L));

        verify(compraRepository, never()).deleteById(any());
    }
}