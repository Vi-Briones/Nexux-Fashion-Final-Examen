package com.Nexus_Fashion.inventario_service.InventarioServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
 
import com.Nexus_Fashion.inventario_service.model.Inventario;
import com.Nexus_Fashion.inventario_service.repository.InventarioRepository;
import com.Nexus_Fashion.inventario_service.service.InventarioService;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
 
@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;
 
    @InjectMocks
    private InventarioService inventarioService;
 
    private Inventario inventarioEjemplo;
 
    @BeforeEach
    void setUp() {
        inventarioEjemplo = Inventario.builder()
                .id(1L)
                .idProducto(10L)
                .sku("NEXUS-POLERA-100")
                .cantidadDisponible(50)
                .ubicacionBodega("Pasillo A - Estante 1")
                .build();
    }
 
    // ---------- listar ----------
 
    @Test
    void testListar_retornaLista() {
        when(inventarioRepository.findAll()).thenReturn(Arrays.asList(inventarioEjemplo));
 
        List<Inventario> resultado = inventarioService.listar();
 
        assertEquals(1, resultado.size());
        verify(inventarioRepository, times(1)).findAll();
    }
 
    @Test
    void testListar_listaVacia() {
        when(inventarioRepository.findAll()).thenReturn(Collections.emptyList());
 
        List<Inventario> resultado = inventarioService.listar();
 
        assertTrue(resultado.isEmpty());
        verify(inventarioRepository, times(1)).findAll();
    }
 
    // ---------- obtenerPorId ----------
 
    @Test
    void testObtenerPorId_encontrado() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventarioEjemplo));
 
        Inventario resultado = inventarioService.obtenerPorId(1L);
 
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(inventarioRepository, times(1)).findById(1L);
    }
 
    @Test
    void testObtenerPorId_noEncontrado_lanzaExcepcion() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());
 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventarioService.obtenerPorId(99L));
 
        assertEquals("Registro de inventario no encontrado", ex.getMessage());
    }
 
    // ---------- guardar ----------
 
    @Test
    void testGuardar_exitoso() {
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioEjemplo);
 
        Inventario resultado = inventarioService.guardar(inventarioEjemplo);
 
        assertNotNull(resultado);
        assertEquals("NEXUS-POLERA-100", resultado.getSku());
        verify(inventarioRepository, times(1)).save(inventarioEjemplo);
    }
 
    @Test
    void testGuardar_stockNegativo_lanzaExcepcion() {
        Inventario inventarioInvalido = Inventario.builder()
                .idProducto(10L)
                .sku("NEXUS-POLERA-100")
                .cantidadDisponible(-5)
                .ubicacionBodega("Pasillo A - Estante 1")
                .build();
 
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> inventarioService.guardar(inventarioInvalido));
 
        assertEquals("La cantidad disponible no puede ser menor a cero", ex.getMessage());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }
 
    @Test
    void testGuardar_stockCero_esValido() {
        Inventario inventarioStockCero = Inventario.builder()
                .idProducto(10L)
                .sku("NEXUS-POLERA-100")
                .cantidadDisponible(0)
                .ubicacionBodega("Pasillo A - Estante 1")
                .build();
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioStockCero);
 
        Inventario resultado = inventarioService.guardar(inventarioStockCero);
 
        assertNotNull(resultado);
        assertEquals(0, resultado.getCantidadDisponible());
        verify(inventarioRepository, times(1)).save(inventarioStockCero);
    }
 
    // ---------- eliminar ----------
 
    @Test
    void testEliminar_exitoso() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventarioEjemplo));
        doNothing().when(inventarioRepository).delete(inventarioEjemplo);
 
        inventarioService.eliminar(1L);
 
        verify(inventarioRepository, times(1)).findById(1L);
        verify(inventarioRepository, times(1)).delete(inventarioEjemplo);
    }
 
    @Test
    void testEliminar_noExiste_lanzaExcepcion() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());
 
        assertThrows(RuntimeException.class, () -> inventarioService.eliminar(99L));
 
        verify(inventarioRepository, never()).delete(any(Inventario.class));
    }
 
    // ---------- validarYDescontarStock ----------
 
    @Test
    void testValidarYDescontarStock_exitoso() {
        when(inventarioRepository.findByIdProducto(10L)).thenReturn(Optional.of(inventarioEjemplo));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioEjemplo);
 
        boolean resultado = inventarioService.validarYDescontarStock(10L, 20);
 
        assertTrue(resultado);
        assertEquals(30, inventarioEjemplo.getCantidadDisponible());
        verify(inventarioRepository, times(1)).save(inventarioEjemplo);
    }
 
    @Test
    void testValidarYDescontarStock_stockInsuficiente_retornaFalse() {
        when(inventarioRepository.findByIdProducto(10L)).thenReturn(Optional.of(inventarioEjemplo));
 
        boolean resultado = inventarioService.validarYDescontarStock(10L, 100);
 
        assertFalse(resultado);
        assertEquals(50, inventarioEjemplo.getCantidadDisponible());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }
 
    @Test
    void testValidarYDescontarStock_stockExacto_retornaTrue() {
        when(inventarioRepository.findByIdProducto(10L)).thenReturn(Optional.of(inventarioEjemplo));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioEjemplo);
 
        boolean resultado = inventarioService.validarYDescontarStock(10L, 50);
 
        assertTrue(resultado);
        assertEquals(0, inventarioEjemplo.getCantidadDisponible());
    }
 
    @Test
    void testValidarYDescontarStock_productoNoExisteEnInventario_lanzaExcepcion() {
        when(inventarioRepository.findByIdProducto(999L)).thenReturn(Optional.empty());
 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventarioService.validarYDescontarStock(999L, 10));
 
        assertEquals("El producto solicitado no está registrado en el inventario", ex.getMessage());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }
}
