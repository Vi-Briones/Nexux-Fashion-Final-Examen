package com.Nexus_Fashion.inventario_service.InventarioDTOTest;

import static org.junit.jupiter.api.Assertions.*;
 
import com.Nexus_Fashion.inventario_service.dto.InventarioDTO;
import com.Nexus_Fashion.inventario_service.model.Inventario;
import org.junit.jupiter.api.Test;
public class InventarioDTOTest {

     // ---------- fromModel ----------
 
    @Test
    void fromModel_RetornaNull_CuandoInventarioEsNull() {
        InventarioDTO resultado = InventarioDTO.fromModel(null);
        assertNull(resultado);
    }
 
    @Test
    void fromModel_MapeaCorrectamenteTodosLosCampos() {
        Inventario inventario = Inventario.builder()
                .id(1L)
                .idProducto(10L)
                .sku("NEXUS-POLERA-100")
                .cantidadDisponible(50)
                .ubicacionBodega("Pasillo A - Estante 1")
                .build();
 
        InventarioDTO dto = InventarioDTO.fromModel(inventario);
 
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getIdProducto());
        assertEquals("NEXUS-POLERA-100", dto.getSku());
        assertEquals(50, dto.getCantidadDisponible());
        assertEquals("Pasillo A - Estante 1", dto.getUbicacionBodega());
    }
 
    // ---------- toModel ----------
 
    @Test
    void toModel_MapeaCorrectamenteTodosLosCampos() {
        InventarioDTO dto = InventarioDTO.builder()
                .id(1L)
                .idProducto(10L)
                .sku("NEXUS-POLERA-100")
                .cantidadDisponible(50)
                .ubicacionBodega("Pasillo A - Estante 1")
                .build();
 
        Inventario inventario = dto.toModel();
 
        assertNotNull(inventario);
        assertEquals(1L, inventario.getId());
        assertEquals(10L, inventario.getIdProducto());
        assertEquals("NEXUS-POLERA-100", inventario.getSku());
        assertEquals(50, inventario.getCantidadDisponible());
        assertEquals("Pasillo A - Estante 1", inventario.getUbicacionBodega());
    }
 
    @Test
    void toModel_ConIdNull_ParaCreacionDeNuevoRegistro() {
        InventarioDTO dto = InventarioDTO.builder()
                .idProducto(20L)
                .sku("NEXUS-JEAN-200")
                .cantidadDisponible(30)
                .ubicacionBodega("Pasillo B - Estante 2")
                .build();
 
        Inventario inventario = dto.toModel();
 
        assertNull(inventario.getId());
        assertEquals("NEXUS-JEAN-200", inventario.getSku());
    }
}
