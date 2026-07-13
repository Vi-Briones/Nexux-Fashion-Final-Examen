package com.Nexus_Fashion.notificacion.InventarioDTOTest;

import static org.junit.jupiter.api.Assertions.*;

import com.Nexus_Fashion.notificacion_service.dto.NotificacionDTO;
import com.Nexus_Fashion.notificacion_service.model.Notificacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class NotificacionDTOTest {

    // ─────────────────────────── fromModel ─────────────────────────

    @Test
    void fromModel_RetornaNull_CuandoNotificacionEsNull() {
        NotificacionDTO resultado = NotificacionDTO.fromModel(null);
        assertNull(resultado);
    }

    @Test
    void fromModel_MapeaCorrectamenteTodosLosCampos() {
        LocalDateTime fecha = LocalDateTime.of(2024, 6, 15, 10, 30);

        Notificacion notificacion = Notificacion.builder()
                .id(1L)
                .idUsuario(5L)
                .tipoEvento("DESPACHO_EN_CAMINO")
                .mensaje("Nexus Fashion: tu pedido está en camino")
                .leido(false)
                .fechaEnvio(fecha)
                .build();

        NotificacionDTO dto = NotificacionDTO.fromModel(notificacion);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(5L, dto.getIdUsuario());
        assertEquals("DESPACHO_EN_CAMINO", dto.getTipoEvento());
        assertEquals("Nexus Fashion: tu pedido está en camino", dto.getMensaje());
        assertEquals(false, dto.getLeido());
        assertEquals(fecha, dto.getFechaEnvio());
    }

    // ─────────────────────────── toModel ───────────────────────────

    @Test
    void toModel_MapeaCorrectamenteTodosLosCampos() {
        LocalDateTime fecha = LocalDateTime.of(2024, 6, 15, 10, 30);

        NotificacionDTO dto = NotificacionDTO.builder()
                .id(1L)
                .idUsuario(5L)
                .tipoEvento("PEDIDO_CONFIRMADO")
                .mensaje("Tu pedido fue confirmado exitosamente")
                .leido(true)
                .fechaEnvio(fecha)
                .build();

        Notificacion notificacion = dto.toModel();

        assertNotNull(notificacion);
        assertEquals(1L, notificacion.getId());
        assertEquals(5L, notificacion.getIdUsuario());
        assertEquals("PEDIDO_CONFIRMADO", notificacion.getTipoEvento());
        assertEquals("Tu pedido fue confirmado exitosamente", notificacion.getMensaje());
        assertEquals(true, notificacion.getLeido());
        assertEquals(fecha, notificacion.getFechaEnvio());
    }

    @Test
    void toModel_ConIdNull_ParaCreacionDeNuevoRegistro() {
        LocalDateTime fecha = LocalDateTime.of(2024, 6, 15, 10, 30);

        NotificacionDTO dto = NotificacionDTO.builder()
                .idUsuario(3L)
                .tipoEvento("PEDIDO_CANCELADO")
                .mensaje("Tu pedido fue cancelado")
                .leido(false)
                .fechaEnvio(fecha)
                .build();

        Notificacion notificacion = dto.toModel();

        assertNull(notificacion.getId());
        assertEquals("PEDIDO_CANCELADO", notificacion.getTipoEvento());
        assertEquals(3L, notificacion.getIdUsuario());
    }

    @Test
    void toModel_ConFechaEnvioNull_AsignaFechaAutomatica() {
        NotificacionDTO dto = NotificacionDTO.builder()
                .idUsuario(2L)
                .tipoEvento("PEDIDO_CONFIRMADO")
                .mensaje("Tu pedido fue confirmado")
                .leido(false)
                .fechaEnvio(null)
                .build();

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        Notificacion notificacion = dto.toModel();
        LocalDateTime despues = LocalDateTime.now().plusSeconds(1);

        assertNotNull(notificacion.getFechaEnvio());
        assertTrue(notificacion.getFechaEnvio().isAfter(antes));
        assertTrue(notificacion.getFechaEnvio().isBefore(despues));
    }
}