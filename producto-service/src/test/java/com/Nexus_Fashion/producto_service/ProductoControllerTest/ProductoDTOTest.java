package com.Nexus_Fashion.producto_service.ProductoControllerTest;

import com.Nexus_Fashion.producto_service.dto.ProductoDTO;
import com.Nexus_Fashion.producto_service.model.Categoria;
import com.Nexus_Fashion.producto_service.model.Producto;
import org.junit.jupiter.api.Test;
 
import static org.junit.jupiter.api.Assertions.*;
 
public class ProductoDTOTest {

    
    // ---------- fromModel ----------
 
    @Test
    void fromModel_RetornaNull_CuandoProductoEsNull() {
        ProductoDTO resultado = ProductoDTO.fromModel(null);
        assertNull(resultado);
    }
 
    @Test
    void fromModel_MapeaCorrectamente_CuandoProductoTieneCategoria() {
        Categoria categoria = new Categoria(1L, "Ropa");
        Producto producto = new Producto(1L, "Polera Negra", 9990.0, 50, categoria);
 
        ProductoDTO dto = ProductoDTO.fromModel(producto);
 
        assertNotNull(dto);
        assertEquals(1L, dto.getIdProducto());
        assertEquals("Polera Negra", dto.getNombre());
        assertEquals(9990.0, dto.getPrecio());
        assertEquals(50, dto.getStock());
        assertEquals(1L, dto.getIdCategoria());
    }
 
    @Test
    void fromModel_IdCategoriaNull_CuandoProductoNoTieneCategoria() {
        Producto producto = new Producto(1L, "Polera Negra", 9990.0, 50, null);
 
        ProductoDTO dto = ProductoDTO.fromModel(producto);
 
        assertNotNull(dto);
        assertNull(dto.getIdCategoria());
    }
 
    // ---------- toModel ----------
 
    @Test
    void toModel_MapeaCorrectamenteATodosLosCampos() {
        ProductoDTO dto = new ProductoDTO(1L, "Polera Negra", 9990.0, 50, 3L);
 
        Producto producto = dto.toModel();
 
        assertNotNull(producto);
        assertEquals(1L, producto.getIdProducto());
        assertEquals("Polera Negra", producto.getNombre());
        assertEquals(9990.0, producto.getPrecio());
        assertEquals(50, producto.getStock());
        assertNotNull(producto.getCategoria());
        assertEquals(3L, producto.getCategoria().getIdCategoria());
    }
 
    @Test
    void toModel_ConIdProductoNull_ParaCreacionDeNuevoProducto() {
        ProductoDTO dto = new ProductoDTO(null, "Pantalón Azul", 19990.0, 30, 2L);
 
        Producto producto = dto.toModel();
 
        assertNull(producto.getIdProducto());
        assertEquals("Pantalón Azul", producto.getNombre());
        assertEquals(2L, producto.getCategoria().getIdCategoria());
    }
}
