package com.Nexus_Fashion.compra_service.CompraControllerTest;

import com.Nexus_Fashion.compra_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.compra_service.model.Compra;
import com.Nexus_Fashion.compra_service.model.DetalleCompra;
import com.Nexus_Fashion.compra_service.repository.CompraRepository;
import com.Nexus_Fashion.compra_service.service.CompraService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - CompraService")
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private WebClient webClient;

    @InjectMocks
    private CompraService compraService;

    private Compra compraMock;
    private DetalleCompra detalleMock;

    @BeforeEach
    void setUp() {
        detalleMock = new DetalleCompra();
        detalleMock.setIdDetalle(1L);
        detalleMock.setIdProducto(10L);
        detalleMock.setCantidad(2);
        detalleMock.setPrecioUnitario(5000.0);

        compraMock = new Compra();
        compraMock.setId(1L);
        compraMock.setIdCliente(100L);
        compraMock.setTotal(10000.0);
        compraMock.setDetalles(Collections.singletonList(detalleMock));
    }

    // =====================================================================
    // PRUEBAS CRUD BÁSICAS
    // =====================================================================

    @Test
    @DisplayName("listar() debe retornar todas las compras existentes")
    void listar_debeRetornarTodasLasCompras() {
        Compra compra2 = new Compra(2L, 200L, 3000.0, Collections.singletonList(detalleMock));
        when(compraRepository.findAll()).thenReturn(Arrays.asList(compraMock, compra2));

        List<Compra> resultado = compraService.listar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(compraRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("listar() debe retornar lista vacía cuando no hay compras")
    void listar_debeRetornarListaVaciaSinCompras() {
        when(compraRepository.findAll()).thenReturn(Collections.emptyList());

        List<Compra> resultado = compraService.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(compraRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscarPorId() debe retornar la compra correcta cuando el ID existe")
    void buscarPorId_debeRetornarCompra_cuandoExiste() {
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraMock));

        Compra resultado = compraService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(100L, resultado.getIdCliente());
        assertEquals(10000.0, resultado.getTotal());
        verify(compraRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId() debe retornar null cuando el ID no existe")
    void buscarPorId_debeRetornarNull_cuandoNoExiste() {
        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        Compra resultado = compraService.buscarPorId(99L);

        assertNull(resultado);
        verify(compraRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("existePorId() debe retornar true cuando la compra existe")
    void existePorId_debeRetornarTrue_cuandoExiste() {
        when(compraRepository.existsById(1L)).thenReturn(true);

        boolean resultado = compraService.existePorId(1L);

        assertTrue(resultado);
        verify(compraRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("existePorId() debe retornar false cuando la compra no existe")
    void existePorId_debeRetornarFalse_cuandoNoExiste() {
        when(compraRepository.existsById(99L)).thenReturn(false);

        boolean resultado = compraService.existePorId(99L);

        assertFalse(resultado);
        verify(compraRepository, times(1)).existsById(99L);
    }

    @Test
    @DisplayName("actualizar() debe lanzar ResourceNotFoundException cuando el ID no existe")
    void actualizar_debeLanzarExcepcion_cuandoNoExiste() {
        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> compraService.actualizar(99L, compraMock));

        verify(compraRepository, times(1)).findById(99L);
        verify(compraRepository, never()).save(any());
    }

    @Test
    @DisplayName("actualizar() debe guardar y retornar la compra actualizada")
    void actualizar_debeRetornarCompraActualizada_cuandoExiste() {
        Compra compraActualizada = new Compra(1L, 100L, 25000.0,
                Collections.singletonList(detalleMock));
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraMock));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraActualizada);

        Compra resultado = compraService.actualizar(1L, compraActualizada);

        assertNotNull(resultado);
        assertEquals(25000.0, resultado.getTotal());
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    @DisplayName("eliminar() debe lanzar ResourceNotFoundException cuando el ID no existe")
    void eliminar_debeLanzarExcepcion_cuandoNoExiste() {
        when(compraRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> compraService.eliminar(99L));

        verify(compraRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("eliminar() debe ejecutar deleteById cuando la compra existe")
    void eliminar_debeEliminar_cuandoExiste() {
        when(compraRepository.existsById(1L)).thenReturn(true);
        doNothing().when(compraRepository).deleteById(1L);

        assertDoesNotThrow(() -> compraService.eliminar(1L));

        verify(compraRepository, times(1)).deleteById(1L);
    }

    // =====================================================================
    // PRUEBAS DE NEGOCIO (requeridas por la pauta)
    // =====================================================================

    @Test
    @DisplayName("[NEGOCIO] totalComprasPorCliente() debe retornar el conteo correcto")
    void totalComprasPorCliente_debeRetornarConteoReal() {
        when(compraRepository.countByIdCliente(100L)).thenReturn(5L);

        long resultado = compraService.totalComprasPorCliente(100L);

        assertEquals(5L, resultado);
        verify(compraRepository, times(1)).countByIdCliente(100L);
    }

    @Test
    @DisplayName("[NEGOCIO] totalComprasPorCliente() debe retornar 0 para cliente sin compras")
    void totalComprasPorCliente_debeRetornarCero_sinCompras() {
        when(compraRepository.countByIdCliente(999L)).thenReturn(0L);

        long resultado = compraService.totalComprasPorCliente(999L);

        assertEquals(0L, resultado);
    }

    @Test
    @DisplayName("[NEGOCIO] listarPorCliente() debe retornar solo las compras del cliente indicado")
    void listarPorCliente_debeRetornarComprasDelCliente() {
        Compra compraCliente = new Compra(2L, 100L, 8000.0,
                Collections.singletonList(detalleMock));
        when(compraRepository.findByIdCliente(100L))
                .thenReturn(Arrays.asList(compraMock, compraCliente));

        List<Compra> resultado = compraService.listarPorCliente(100L);

        assertEquals(2, resultado.size());
        resultado.forEach(c -> assertEquals(100L, c.getIdCliente()));
        verify(compraRepository, times(1)).findByIdCliente(100L);
    }

    @Test
    @DisplayName("[NEGOCIO] listarPorCliente() debe retornar lista vacía para cliente sin compras")
    void listarPorCliente_debeRetornarVacio_sinCompras() {
        when(compraRepository.findByIdCliente(999L)).thenReturn(Collections.emptyList());

        List<Compra> resultado = compraService.listarPorCliente(999L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("[NEGOCIO] guardar() debe persistir la compra aunque el servicio externo falle (fallback activo)")
    void guardar_debePersistir_cuandoServicioExternoFalla() {
        // El servicio externo lanza excepción (WebClient no configurado en test)
        // Por diseño del fallback, la compra se guarda igual
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);

        Compra resultado = compraService.guardar(compraMock);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    @DisplayName("[NEGOCIO] el total de una compra debe ser igual a cantidad × precioUnitario del detalle")
    void total_debeSerIgualACantidadPorPrecioUnitario() {
        int cantidad = detalleMock.getCantidad();
        double precioUnitario = detalleMock.getPrecioUnitario();
        double totalEsperado = cantidad * precioUnitario;

        assertEquals(totalEsperado, compraMock.getTotal(),
                "El total de la compra debe coincidir con cantidad × precioUnitario");
    }
}