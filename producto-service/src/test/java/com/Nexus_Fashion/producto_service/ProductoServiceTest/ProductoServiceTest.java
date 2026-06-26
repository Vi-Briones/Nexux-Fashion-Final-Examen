package com.Nexus_Fashion.producto_service.ProductoServiceTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Nexus_Fashion.producto_service.model.Categoria;
import com.Nexus_Fashion.producto_service.model.Producto;
import com.Nexus_Fashion.producto_service.repository.ProductoRepository;
import com.Nexus_Fashion.producto_service.service.ProductoService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;
 
    @InjectMocks
    private ProductoService productoService;
 
    private Producto producto;
    private Categoria categoria;
 
    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Ropa");
        producto = new Producto(1L, "Polera Negra", 9990.0, 50, categoria);
    }
 
    // ---------- guardar ----------
 
    @Test
    void guardar_Exito_CuandoProductoNuevoYNoExisteNombre() {
        producto.setIdProducto(null);
        when(productoRepository.existsByNombre("Polera Negra")).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            p.setIdProducto(1L);
            return p;
        });
 
        Producto resultado = productoService.guardar(producto);
 
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
        assertEquals("Polera Negra", resultado.getNombre());
        verify(productoRepository, times(1)).existsByNombre("Polera Negra");
        verify(productoRepository, times(1)).save(producto);
    }
 
    @Test
    void guardar_LanzaExcepcion_CuandoNombreYaExiste() {
        producto.setIdProducto(null);
        when(productoRepository.existsByNombre("Polera Negra")).thenReturn(true);
 
        RuntimeException ex = assertThrows(RuntimeException.class, () -> productoService.guardar(producto));
 
        assertEquals("El producto ya existe en el inventario.", ex.getMessage());
        verify(productoRepository, never()).save(any(Producto.class));
    }
 
    @Test
    void guardar_NoValidaDuplicado_CuandoProductoYaTieneId() {
        // producto.idProducto != null -> no debería llamar a existsByNombre
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
 
        Producto resultado = productoService.guardar(producto);
 
        assertNotNull(resultado);
        verify(productoRepository, never()).existsByNombre(any());
        verify(productoRepository, times(1)).save(producto);
    }
 
    @Test
    void guardar_PropagaExcepcion_CuandoRepositoryFalla() {
        producto.setIdProducto(null);
        when(productoRepository.existsByNombre(any())).thenReturn(false);
        when(productoRepository.save(any(Producto.class))).thenThrow(new RuntimeException("Error de BD"));
 
        assertThrows(RuntimeException.class, () -> productoService.guardar(producto));
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
 
    // ---------- listar ----------
 
    @Test
    void listar_RetornaListaDeProductos() {
        Producto producto2 = new Producto(2L, "Pantalón Azul", 19990.0, 30, categoria);
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto, producto2));
 
        List<Producto> resultado = productoService.listar();
 
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }
 
    @Test
    void listar_RetornaListaVacia_CuandoNoHayProductos() {
        when(productoRepository.findAll()).thenReturn(List.of());
 
        List<Producto> resultado = productoService.listar();
 
        assertTrue(resultado.isEmpty());
        verify(productoRepository, times(1)).findAll();
    }
 
    // ---------- buscarPorId ----------
 
    @Test
    void buscarPorId_RetornaProducto_CuandoExiste() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
 
        Producto resultado = productoService.buscarPorId(1L);
 
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProducto());
        verify(productoRepository, times(1)).findById(1L);
    }
 
    @Test
    void buscarPorId_RetornaNull_CuandoNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
 
        Producto resultado = productoService.buscarPorId(99L);
 
        assertNull(resultado);
        verify(productoRepository, times(1)).findById(99L);
    }
 
    // ---------- actualizar ----------
 
    @Test
    void actualizar_Exito_CuandoProductoExiste() {
        Categoria nuevaCategoria = new Categoria(2L, "Calzado");
        Producto datosActualizados = new Producto(null, "Polera Blanca", 12990.0, 20, nuevaCategoria);
 
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        Producto resultado = productoService.actualizar(1L, datosActualizados);
 
        assertNotNull(resultado);
        assertEquals("Polera Blanca", resultado.getNombre());
        assertEquals(12990.0, resultado.getPrecio());
        assertEquals(20, resultado.getStock());
        assertEquals(2L, resultado.getCategoria().getIdCategoria());
        verify(productoRepository, times(1)).save(producto);
    }
 
    @Test
    void actualizar_MantieneCategoriaOriginal_CuandoCategoriaActualizadaEsNull() {
        Producto datosActualizados = new Producto(null, "Polera Blanca", 12990.0, 20, null);
 
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        Producto resultado = productoService.actualizar(1L, datosActualizados);
 
        assertNotNull(resultado.getCategoria());
        assertEquals(1L, resultado.getCategoria().getIdCategoria());
    }
 
    @Test
    void actualizar_LanzaExcepcion_CuandoProductoNoExiste() {
        Producto datosActualizados = new Producto(null, "Polera Blanca", 12990.0, 20, categoria);
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productoService.actualizar(99L, datosActualizados));
 
        assertEquals("Producto no encontrado con ID: 99", ex.getMessage());
        verify(productoRepository, never()).save(any(Producto.class));
    }
 
    // ---------- eliminar ----------
 
    @Test
    void eliminar_Exito_CuandoProductoExiste() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);
 
        assertDoesNotThrow(() -> productoService.eliminar(1L));
 
        verify(productoRepository, times(1)).existsById(1L);
        verify(productoRepository, times(1)).deleteById(1L);
    }
 
    @Test
    void eliminar_LanzaExcepcion_CuandoProductoNoExiste() {
        when(productoRepository.existsById(99L)).thenReturn(false);
 
        RuntimeException ex = assertThrows(RuntimeException.class, () -> productoService.eliminar(99L));
 
        assertEquals("Producto no encontrado con ID: 99", ex.getMessage());
        verify(productoRepository, never()).deleteById(anyLong());
    }
}
