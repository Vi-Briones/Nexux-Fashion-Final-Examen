package com.Nexus_Fashion.recomendaciones_service.GlobalExceptionHandlerTest;

import static org.junit.jupiter.api.Assertions.*;

import com.Nexus_Fashion.recomendaciones_service.assemblers.RecomendacionModelAssembler;
import com.Nexus_Fashion.recomendaciones_service.dto.RecomendacionDTO;
import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RecomendacionModelAssemblerTest {

    private RecomendacionModelAssembler assembler;
    private Recomendacion recomendacionEjemplo;

    @BeforeEach
    void setUp() {
        assembler = new RecomendacionModelAssembler();

        recomendacionEjemplo = new Recomendacion(
                1L, 5L, 10L,
                "PRODUCTO_SIMILAR",
                "Te recomendamos este producto basado en tus compras anteriores",
                85.0
        );

        // Necesario para que linkTo(methodOn(...)) pueda resolver la URL base
        // sin tener un contexto web real levantado.
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    void toModel_GeneraEntityModelConDatosCorrectos() {
        EntityModel<RecomendacionDTO> resultado = assembler.toModel(recomendacionEjemplo);

        assertNotNull(resultado);
        assertNotNull(resultado.getContent());
        assertEquals(1L, resultado.getContent().getId());
        assertEquals(5L, resultado.getContent().getIdCliente());
        assertEquals(10L, resultado.getContent().getIdProducto());
        assertEquals("PRODUCTO_SIMILAR", resultado.getContent().getTipoRecomendacion());
        assertEquals(85.0, resultado.getContent().getPuntajeAfinidad());
    }

    @Test
    void toModel_IncluyeLinkSelf() {
        EntityModel<RecomendacionDTO> resultado = assembler.toModel(recomendacionEjemplo);

        assertTrue(resultado.hasLink("self"));
        Link self = resultado.getLink("self").orElse(null);
        assertNotNull(self);
        assertTrue(self.getHref().contains("/v2/recomendaciones/1"));
    }

    @Test
    void toModel_IncluyeLinkRecomendaciones() {
        EntityModel<RecomendacionDTO> resultado = assembler.toModel(recomendacionEjemplo);

        assertTrue(resultado.hasLink("recomendaciones"));
        Link recomendaciones = resultado.getLink("recomendaciones").orElse(null);
        assertNotNull(recomendaciones);
        assertTrue(recomendaciones.getHref().contains("/v2/recomendaciones"));
    }
}