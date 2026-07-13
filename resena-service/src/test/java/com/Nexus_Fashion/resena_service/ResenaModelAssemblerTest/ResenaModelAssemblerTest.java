package com.Nexus_Fashion.resena_service.ResenaModelAssemblerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import com.Nexus_Fashion.resena_service.assemblers.ResenaModelAssembler;
import com.Nexus_Fashion.resena_service.dto.ResenaDTO;

public class ResenaModelAssemblerTest {

    @Test
    void testToModel_generaLinksCorrectamente() {
        ResenaModelAssembler assembler = new ResenaModelAssembler();
        ResenaDTO resenaDTO = new ResenaDTO(1L, 100L, "Juan Perez", 5, "Excelente producto");

        EntityModel<ResenaDTO> entityModel = assembler.toModel(resenaDTO);

        assertNotNull(entityModel);
        assertEquals(resenaDTO, entityModel.getContent());

        // Verifica que tenga el link "self"
        assertTrue(entityModel.hasLink("self"));
        assertTrue(entityModel.getLink("self").isPresent());
        assertTrue(entityModel.getLink("self").get().getHref().contains("/resenas/v2/1"));

        // Verifica que tenga el link "resenas"
        assertTrue(entityModel.hasLink("resenas"));
        assertTrue(entityModel.getLink("resenas").isPresent());
        assertTrue(entityModel.getLink("resenas").get().getHref().contains("/resenas/v2"));
    }
}
