package com.Nexus_Fashion.recomendaciones_service.InventarioDTOTest;

import static org.junit.jupiter.api.Assertions.*;

import com.Nexus_Fashion.recomendaciones_service.dto.RecomendacionDTO;
import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;
import org.junit.jupiter.api.Test;

public class RecomendacionDTOTest {

    // ─────────────────────────── fromModel ─────────────────────────

    @Test
    void fromModel_RetornaNull_CuandoRecomendacionEsNull() {
        RecomendacionDTO resultado = RecomendacionDTO.fromModel(null);
        assertNull(resultado);
    }

    @Test
    void fromModel_MapeaCorrectamenteTodosLosCampos() {
        Recomendacion recomendacion = new Recomendacion(
                1L, 5L, 10L,
                "PRODUCTO_SIMILAR",
                "Te recomendamos este producto basado en tus compras anteriores",
                85.0
        );

        RecomendacionDTO dto = RecomendacionDTO.fromModel(recomendacion);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(5L, dto.getIdCliente());
        assertEquals(10L, dto.getIdProducto());
        assertEquals("PRODUCTO_SIMILAR", dto.getTipoRecomendacion());
        assertEquals("Te recomendamos este producto basado en tus compras anteriores", dto.getComentario());
        assertEquals(85.0, dto.getPuntajeAfinidad());
    }

    // ─────────────────────────── toModel ───────────────────────────

    @Test
    void toModel_MapeaCorrectamenteTodosLosCampos() {
        RecomendacionDTO dto = new RecomendacionDTO(
                1L, 5L, 10L,
                "TENDENCIA",
                "Producto en tendencia esta temporada",
                90.0
        );

        Recomendacion recomendacion = dto.toModel();

        assertNotNull(recomendacion);
        assertEquals(1L, recomendacion.getId());
        assertEquals(5L, recomendacion.getIdCliente());
        assertEquals(10L, recomendacion.getIdProducto());
        assertEquals("TENDENCIA", recomendacion.getTipoRecomendacion());
        assertEquals("Producto en tendencia esta temporada", recomendacion.getComentario());
        assertEquals(90.0, recomendacion.getPuntajeAfinidad());
    }

    @Test
    void toModel_ConIdNull_ParaCreacionDeNuevoRegistro() {
        RecomendacionDTO dto = new RecomendacionDTO(
                null, 3L, 7L,
                "COMPLEMENTARIO",
                "Producto complementario a tu compra",
                70.0
        );

        Recomendacion recomendacion = dto.toModel();

        assertNull(recomendacion.getId());
        assertEquals(3L, recomendacion.getIdCliente());
        assertEquals("COMPLEMENTARIO", recomendacion.getTipoRecomendacion());
    }

    @Test
    void toModel_PuntajeAfinidadMinimo_MapeaCorrectamente() {
        RecomendacionDTO dto = new RecomendacionDTO(
                null, 1L, 1L, "TIPO", "Comentario", 0.0
        );

        Recomendacion recomendacion = dto.toModel();

        assertEquals(0.0, recomendacion.getPuntajeAfinidad());
    }

    @Test
    void toModel_PuntajeAfinidadMaximo_MapeaCorrectamente() {
        RecomendacionDTO dto = new RecomendacionDTO(
                null, 1L, 1L, "TIPO", "Comentario", 100.0
        );

        Recomendacion recomendacion = dto.toModel();

        assertEquals(100.0, recomendacion.getPuntajeAfinidad());
    }
}