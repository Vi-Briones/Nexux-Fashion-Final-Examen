package com.Nexus_Fashion.venta_service.VentaServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.Nexus_Fashion.venta_service.dto.VentaDTO;
import com.Nexus_Fashion.venta_service.model.DetalleVenta;
import com.Nexus_Fashion.venta_service.model.MetodoPago;
import com.Nexus_Fashion.venta_service.model.Venta;
import com.Nexus_Fashion.venta_service.repository.VentaRepository;
import com.Nexus_Fashion.venta_service.service.VentaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;
 
    @InjectMocks
    private VentaService ventaService;
 
    private MetodoPago metodoPago;
    private Venta ventaEjemplo;
    private VentaDTO ventaDtoEjemplo;
    private VentaDTO.DetalleItem detalleItem;
 
    @BeforeEach
    void setUp() {
        metodoPago = new MetodoPago(1L, "Tarjeta");
 
        detalleItem = new VentaDTO.DetalleItem(10L, 2);
 
        DetalleVenta detalle = new DetalleVenta(null, null, 10L, 2);
        ventaEjemplo = new Venta(1L, 5L, new Date(), 50000.0, metodoPago,
                Collections.singletonList(detalle));
 
        ventaDtoEjemplo = new VentaDTO(
                null,
                5L,
                new Date(),
                50000.0,
                1L,
                Collections.singletonList(detalleItem)
        );
    }

    @Test
    void testGuardar_exitoso() {
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaEjemplo);
 
        VentaDTO resultado = ventaService.guardar(ventaDtoEjemplo);
 
        assertNotNull(resultado);
        assertEquals(50000.0, resultado.getTotal());
        assertEquals(1L, resultado.getIdVenta());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }
 
    @Test
    void testGuardar_sinDetalles_lanzaExcepcion() {
        VentaDTO dtoSinDetalles = new VentaDTO(null, 5L, new Date(), 50000.0, 1L,
                Collections.emptyList());
 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ventaService.guardar(dtoSinDetalles));
 
        assertTrue(ex.getMessage().contains("al menos un detalle"));
        verify(ventaRepository, never()).save(any());
    }
 
    @Test
    void testGuardar_totalInvalido_lanzaExcepcion() {
        VentaDTO dtoTotalCero = new VentaDTO(null, 5L, new Date(), 0.0, 1L,
                Collections.singletonList(detalleItem));
 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> ventaService.guardar(dtoTotalCero));
 
        assertTrue(ex.getMessage().contains("mayor a 0"));
        verify(ventaRepository, never()).save(any());
    }
 
    @Test
    void testGuardar_totalNegativo_lanzaExcepcion() {
        VentaDTO dtoTotalNegativo = new VentaDTO(null, 5L, new Date(), -100.0, 1L,
                Collections.singletonList(detalleItem));
 
        assertThrows(RuntimeException.class,
                () -> ventaService.guardar(dtoTotalNegativo));
 
        verify(ventaRepository, never()).save(any());
    }
 
 
    @Test
    void testListar_retornaLista() {
        when(ventaRepository.findAll()).thenReturn(Arrays.asList(ventaEjemplo));
 
        List<VentaDTO> resultado = ventaService.listar();
 
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdVenta());
        verify(ventaRepository, times(1)).findAll();
    }
 
    @Test
    void testListar_listaVacia() {
        when(ventaRepository.findAll()).thenReturn(Collections.emptyList());
 
        List<VentaDTO> resultado = ventaService.listar();
 
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_encontrado() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaEjemplo));
 
        VentaDTO resultado = ventaService.buscarPorId(1L);
 
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdVenta());
        verify(ventaRepository, times(1)).findById(1L);
    }
 
    @Test
    void testBuscarPorId_noEncontrado_retornaNull() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
 
        VentaDTO resultado = ventaService.buscarPorId(99L);
 
        assertNull(resultado);
        verify(ventaRepository, times(1)).findById(99L);
    }
 
    @Test
    void testActualizar_exitoso() {
        Venta ventaActualizada = new Venta(1L, 5L, new Date(), 75000.0, metodoPago,
                Collections.emptyList());
 
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(ventaEjemplo));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaActualizada);
 
        VentaDTO dtoActualizado = new VentaDTO(1L, 5L, new Date(), 75000.0, 1L,
                Collections.emptyList());
 
        VentaDTO resultado = ventaService.actualizar(1L, dtoActualizado);
 
        assertNotNull(resultado);
        assertEquals(75000.0, resultado.getTotal());
        verify(ventaRepository, times(1)).findById(1L);
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }
 
    @Test
    void testActualizar_noExiste_lanzaExcepcion() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());
 
        VentaDTO dtoActualizado = new VentaDTO(99L, 5L, new Date(), 75000.0, 1L,
                Collections.emptyList());
 
        assertThrows(RuntimeException.class,
                () -> ventaService.actualizar(99L, dtoActualizado));
 
        verify(ventaRepository, never()).save(any());
    }
 
    @Test
    void testEliminar_exitoso() {
        when(ventaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(ventaRepository).deleteById(1L);
 
        ventaService.eliminar(1L);
 
        verify(ventaRepository, times(1)).deleteById(1L);
    }
 
    @Test
    void testEliminar_noExiste_lanzaExcepcion() {
        when(ventaRepository.existsById(99L)).thenReturn(false);
 
        assertThrows(RuntimeException.class, () -> ventaService.eliminar(99L));
 
        verify(ventaRepository, never()).deleteById(any());
    }
 
 
    @Test
    void testCalcularIVA_montoValido() {
        double resultado = ventaService.calcularIVA(10000.0);
        assertEquals(1900.0, resultado);
    }
 
    @Test
    void testCalcularIVA_montoNegativo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ventaService.calcularIVA(-500.0));
        assertTrue(ex.getMessage().contains("negativo"));
    }
 
    @Test
    void testCalcularIVA_montoCero_retornaCero() {
        double resultado = ventaService.calcularIVA(0.0);
        assertEquals(0.0, resultado);
    }
 
    @Test
    void testAplicarDescuento_casoValido() {
        // 15% de 20000 = 3000
        double resultado = ventaService.aplicarDescuento(20000.0, 15.0);
        assertEquals(3000.0, resultado);
    }
 
    @Test
    void testAplicarDescuento_porcentajeCero_retornaCero() {
        double resultado = ventaService.aplicarDescuento(20000.0, 0.0);
        assertEquals(0.0, resultado);
    }
 
    @Test
    void testAplicarDescuento_porcentaje100_retornaMontoCompleto() {
        double resultado = ventaService.aplicarDescuento(20000.0, 100.0);
        assertEquals(20000.0, resultado);
    }
 
    @Test
    void testAplicarDescuento_porcentajeSuperior100_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ventaService.aplicarDescuento(20000.0, 150.0));
        assertTrue(ex.getMessage().contains("entre 0 y 100"));
    }
 
    @Test
    void testAplicarDescuento_porcentajeNegativo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> ventaService.aplicarDescuento(20000.0, -10.0));
    }
 
    @Test
    void testCalcularRecargo_tarjetaCredito_retorna5Porciento() {
        double resultado = ventaService.calcularRecargoPorMetodoPago(10000.0, "TARJETA_CREDITO");
        assertEquals(500.0, resultado);
    }
 
    @Test
    void testCalcularRecargo_efectivo_retornaCero() {
        double resultado = ventaService.calcularRecargoPorMetodoPago(10000.0, "EFECTIVO");
        assertEquals(0.0, resultado);
    }
 
    @Test
    void testCalcularRecargo_otroMetodo_retornaCero() {
        double resultado = ventaService.calcularRecargoPorMetodoPago(10000.0, "TRANSFERENCIA");
        assertEquals(0.0, resultado);
    }
 
    @Test
    void testCalcularRecargo_montoNegativo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> ventaService.calcularRecargoPorMetodoPago(-100.0, "TARJETA_CREDITO"));
        assertTrue(ex.getMessage().contains("negativo"));
    }
}
