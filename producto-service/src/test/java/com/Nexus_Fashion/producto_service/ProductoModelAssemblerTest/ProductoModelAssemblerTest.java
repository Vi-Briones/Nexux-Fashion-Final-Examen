package com.Nexus_Fashion.producto_service.ProductoModelAssemblerTest;


import com.Nexus_Fashion.producto_service.assemblers.ProductoModelAssembler;
import com.Nexus_Fashion.producto_service.dto.ProductoDTO;
import com.Nexus_Fashion.producto_service.model.Categoria;
import com.Nexus_Fashion.producto_service.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
 
import static org.junit.jupiter.api.Assertions.*;
public class ProductoModelAssemblerTest {

    private ProductoModelAssembler assembler;
    private Producto producto;
 
    @BeforeEach
    void setUp() {
        assembler = new ProductoModelAssembler();
        Categoria categoria = new Categoria(1L, "Ropa");
        producto = new Producto(1L, "Polera Negra", 9990.0, 50, categoria);
 
        // Necesario para que linkTo(methodOn(...)) pueda resolver la URL base
        // sin tener un contexto web real levantado.
        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }
 
    @Test
    void toModel_GeneraEntityModelConDatosCorrectos() {
        EntityModel<ProductoDTO> resultado = assembler.toModel(producto);
 
        assertNotNull(resultado);
        assertNotNull(resultado.getContent());
        assertEquals(1L, resultado.getContent().getIdProducto());
        assertEquals("Polera Negra", resultado.getContent().getNombre());
    }
 
    @Test
    void toModel_IncluyeLinkSelf() {
        EntityModel<ProductoDTO> resultado = assembler.toModel(producto);
 
        assertTrue(resultado.hasLink("self"));
        Link self = resultado.getLink("self").orElse(null);
        assertNotNull(self);
        assertTrue(self.getHref().contains("/productos/1"));
    }
 
    @Test
    void toModel_IncluyeLinkTodosLosProductos() {
        EntityModel<ProductoDTO> resultado = assembler.toModel(producto);
 
        assertTrue(resultado.hasLink("todos-los-productos"));
        Link todos = resultado.getLink("todos-los-productos").orElse(null);
        assertNotNull(todos);
        assertTrue(todos.getHref().contains("/productos"));
    }
}
