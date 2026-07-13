package Nexus_Fashion.envio_service.AssemblerTest;

import static org.junit.jupiter.api.Assertions.*;

import Nexus_Fashion.envio_service.assemblers.EnvioModelAssembler;
import Nexus_Fashion.envio_service.dto.EnvioDTO;
import Nexus_Fashion.envio_service.model.Envio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Collections;

public class EnvioModelAssemblerTest {

    private EnvioModelAssembler assembler;
    private Envio envioEjemplo;

    @BeforeEach
    void setUp() {
        assembler = new EnvioModelAssembler();
        envioEjemplo = Envio.builder()
                .idCompra(1L)
                .estadoEnvio("PENDIENTE")
                .fechaEnvio(LocalDateTime.now())
                .detalles(Collections.emptyList())
                .build();

        RequestContextHolder.setRequestAttributes(
                new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @Test
    void toModel_GeneraEntityModelConDatosCorrectos() {
     
        EntityModel<EnvioDTO> resultado = assembler.toModel(envioEjemplo);
        assertNotNull(resultado);
        assertNotNull(resultado.getContent());
        assertEquals(1L, resultado.getContent().getIdCompra());
    }

    @Test
    void toModel_IncluyeLinkTodosLosEnvios() {
        EntityModel<EnvioDTO> resultado = assembler.toModel(envioEjemplo);
        assertTrue(resultado.hasLink("todos-los-envios"));
        Link link = resultado.getLink("todos-los-envios").orElse(null);
        assertNotNull(link);
        assertTrue(link.getHref().contains("/envios"));
    }

    @Test
    void toModel_IncluyeLinkVerificarExistencia() {
       
        EntityModel<EnvioDTO> resultado = assembler.toModel(envioEjemplo);
        assertTrue(resultado.hasLink("verificar-existencia"));
        Link link = resultado.getLink("verificar-existencia").orElse(null);
        assertNotNull(link);
        assertTrue(link.getHref().contains("/envios"));
    }

    @Test
    void toModel_ContentNoEsNull() {
        EntityModel<EnvioDTO> resultado = assembler.toModel(envioEjemplo);
        assertNotNull(resultado.getContent());
    }
}