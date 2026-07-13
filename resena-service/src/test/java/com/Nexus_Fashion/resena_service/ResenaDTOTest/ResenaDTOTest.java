package com.Nexus_Fashion.resena_service.ResenaDTOTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.Nexus_Fashion.resena_service.dto.ResenaDTO;
import com.Nexus_Fashion.resena_service.model.Resena;

public class ResenaDTOTest {

    @Test
    void fromModel_MapeaCorrectamenteTodosLosCampos() {
        Resena resena = new Resena(1L, 100L, "Juan Perez", 5, "Excelente producto");

        ResenaDTO dto = ResenaDTO.fromModel(resena);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getIdCompra());
        assertEquals("Juan Perez", dto.getCliente());
        assertEquals(5, dto.getCalificacion());
        assertEquals("Excelente producto", dto.getComentario());
    }

    // ---------- toModel ----------

    @Test
    void toModel_MapeaCorrectamenteTodosLosCampos() {
        ResenaDTO dto = new ResenaDTO(1L, 100L, "Juan Perez", 5, "Excelente producto");

        Resena resena = dto.toModel();

        assertNotNull(resena);
        assertEquals(1L, resena.getId());
        assertEquals(100L, resena.getIdCompra());
        assertEquals("Juan Perez", resena.getCliente());
        assertEquals(5, resena.getCalificacion());
        assertEquals("Excelente producto", resena.getComentario());
    }

    @Test
    void toModel_ConIdNull_ParaCreacionDeNuevoRegistro() {
        ResenaDTO dto = new ResenaDTO(null, 200L, "Maria Lopez", 3, "Producto correcto, nada especial");

        Resena resena = dto.toModel();

        assertNull(resena.getId());
        assertEquals("Maria Lopez", resena.getCliente());
    }

    @Test
    void toModel_ConCalificacionNull_AsignaCero() {
        ResenaDTO dto = new ResenaDTO(null, 200L, "Maria Lopez", null, "Sin calificación");

        Resena resena = dto.toModel();

        assertEquals(0, resena.getCalificacion());
    }
}
