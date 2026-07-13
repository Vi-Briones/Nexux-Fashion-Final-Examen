package com.Nexus_Fashion.soporte_servicee.SoporteDTOTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.Nexus_Fashion.soporte_service.dto.SoporteDTO;
import com.Nexus_Fashion.soporte_service.model.Soporte;

public class SoporteDTOTest {

    @Test
    void fromModel_RetornaNull_CuandoSoporteEsNull() {
        assertNull(SoporteDTO.fromModel(null));
    }

    @Test
    void fromModel_MapeaCorrectamenteTodosLosCampos() {
        LocalDateTime ahora = LocalDateTime.now();
        Soporte soporte = new Soporte(1L, 10L, "Asunto", "Descripcion", "PENDIENTE", "ALTA", ahora, null);

        SoporteDTO dto = SoporteDTO.fromModel(soporte);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getIdCliente());
        assertEquals("Asunto", dto.getAsunto());
        assertEquals("Descripcion", dto.getDescripcion());
        assertEquals("PENDIENTE", dto.getEstado());
        assertEquals("ALTA", dto.getPrioridad());
        assertEquals(ahora, dto.getFechaCreacion());
    }

    @Test
    void toModel_ConIdNull_AsignaFechaCreacionAutomatica() {
        SoporteDTO dto = new SoporteDTO(null, 10L, "Asunto", "Descripcion", "PENDIENTE", "ALTA", null, null);

        Soporte soporte = dto.toModel();

        assertNull(soporte.getId());
        assertNotNull(soporte.getFechaCreacion());
    }

    @Test
    void toModel_ConIdExistente_RespetaFechaCreacionOriginal() {
        LocalDateTime fechaOriginal = LocalDateTime.of(2026, 1, 1, 10, 0);
        SoporteDTO dto = new SoporteDTO(1L, 10L, "Asunto", "Descripcion", "PENDIENTE", "ALTA", fechaOriginal, null);

        Soporte soporte = dto.toModel();

        assertEquals(1L, soporte.getId());
        assertEquals(fechaOriginal, soporte.getFechaCreacion());
    }
}
