package com.Nexus_Fashion.soporte_servicee.SoporteControllerTest.SoporteModelAssemblerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import com.Nexus_Fashion.soporte_service.assemblers.SoporteModelAssembler;
import com.Nexus_Fashion.soporte_service.model.Soporte;

public class SoporteModelAssemblerTest {

    @Test
    void testToModel_generaLinksCorrectamente() {
        SoporteModelAssembler assembler = new SoporteModelAssembler();
        Soporte soporte = new Soporte(1L, 10L, "Asunto", "Descripcion", "PENDIENTE", "ALTA",
                LocalDateTime.now(), null);

        EntityModel<Soporte> entityModel = assembler.toModel(soporte);

        assertNotNull(entityModel);
        assertEquals(soporte, entityModel.getContent());
        assertTrue(entityModel.hasLink("self"));
        assertTrue(entityModel.getLink("self").get().getHref().contains("/soportes/v2/1"));
        assertTrue(entityModel.hasLink("soportes"));
        assertTrue(entityModel.getLink("soportes").get().getHref().contains("/soportes/v2"));
    }
}
