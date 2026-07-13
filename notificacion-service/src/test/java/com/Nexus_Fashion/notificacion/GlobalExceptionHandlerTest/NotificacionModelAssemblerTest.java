package com.Nexus_Fashion.notificacion.GlobalExceptionHandlerTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.Nexus_Fashion.notificacion_service.assemblers.NotificacionModelAssembler;
import com.Nexus_Fashion.notificacion_service.model.Notificacion;

public class NotificacionModelAssemblerTest {

    private NotificacionModelAssembler assembler;
    private Notificacion notificacionEjemplo;
    Random random = new Random();
 
    @BeforeEach
    void setUp() {
        assembler = new NotificacionModelAssembler();
        notificacionEjemplo = Notificacion.builder()
                .id(1L)
                .idUsuario(1L)
                .tipoEvento("DESCPACHO_EN_CAMINO")
                .mensaje("Nexus fashion: presenta novedades")
                .leido(random.nextBoolean())
                .fechaEnvio(LocalDateTime.now())
                .build();
 
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }
 
    @Test
    void toModel_GeneraEntityModelConDatosCorrectos() {
        EntityModel<Notificacion> resultado = assembler.toModel(notificacionEjemplo);
 
        assertNotNull(resultado);
        assertEquals(notificacionEjemplo.getId(), resultado.getContent().getId());
        assertEquals(notificacionEjemplo.getIdUsuario(), resultado.getContent().getIdUsuario());
        assertEquals(notificacionEjemplo.getTipoEvento(), resultado.getContent().getTipoEvento());
        assertEquals(notificacionEjemplo.getMensaje(), resultado.getContent().getMensaje());
        assertEquals(notificacionEjemplo.getLeido(), resultado.getContent().getLeido());
        assertEquals(notificacionEjemplo.getFechaEnvio(), resultado.getContent().getFechaEnvio());
    }
 
    @Test
    void toModel_IncluyeLinkSelf() {
        EntityModel<Notificacion> resultado = assembler.toModel(notificacionEjemplo);
 
        assertTrue(resultado.hasLink("self"));
        Link self = resultado.getLink("self").orElse(null);
        assertNotNull(self);
        assertTrue(self.getHref().contains("/notificaciones/v2/1"));
    }
 
    @Test
    void toModel_IncluyeLinkInventarios() {
        EntityModel<Notificacion> resultado = assembler.toModel(notificacionEjemplo);
 
        assertTrue(resultado.hasLink("notificaciones"));
        Link notificaciones = resultado.getLink("notificaciones").orElse(null);
        assertNotNull(notificaciones);
        assertTrue(notificaciones.getHref().contains("/notificaciones/v2"));
    }
}
