package com.Nexus_Fashion.inventario_service.GlobalExceptionHandlerTest;

import static org.junit.jupiter.api.Assertions.*;
 
import com.Nexus_Fashion.inventario_service.assemblers.InventarioModelAssembler;
import com.Nexus_Fashion.inventario_service.model.Inventario;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class InventarioModelAssemblerTest {

    private InventarioModelAssembler assembler;
    private Inventario inventarioEjemplo;
 
    @BeforeEach
    void setUp() {
        assembler = new InventarioModelAssembler();
        inventarioEjemplo = Inventario.builder()
                .id(1L)
                .idProducto(10L)
                .sku("NEXUS-POLERA-100")
                .cantidadDisponible(50)
                .ubicacionBodega("Pasillo A - Estante 1")
                .build();
 
        // Necesario para que linkTo(methodOn(...)) pueda resolver la URL base
        // sin tener un contexto web real levantado.
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }
 
    @Test
    void toModel_GeneraEntityModelConDatosCorrectos() {
        EntityModel<Inventario> resultado = assembler.toModel(inventarioEjemplo);
 
        assertNotNull(resultado);
        assertNotNull(resultado.getContent());
        assertEquals(1L, resultado.getContent().getId());
        assertEquals("NEXUS-POLERA-100", resultado.getContent().getSku());
    }
 
    @Test
    void toModel_IncluyeLinkSelf() {
        EntityModel<Inventario> resultado = assembler.toModel(inventarioEjemplo);
 
        assertTrue(resultado.hasLink("self"));
        Link self = resultado.getLink("self").orElse(null);
        assertNotNull(self);
        assertTrue(self.getHref().contains("/inventarios/v2/1"));
    }
 
    @Test
    void toModel_IncluyeLinkInventarios() {
        EntityModel<Inventario> resultado = assembler.toModel(inventarioEjemplo);
 
        assertTrue(resultado.hasLink("inventarios"));
        Link inventarios = resultado.getLink("inventarios").orElse(null);
        assertNotNull(inventarios);
        assertTrue(inventarios.getHref().contains("/inventarios/v2"));
    }
}
