package com.Nexus_Fashion.resena_service.SwaggerConfigTest;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import com.Nexus_Fashion.resena_service.config.SwaggerConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SwaggerConfigTest {

    @Test
    void testCustomOpenAPI_retornaConfiguracionCorrecta() {
        SwaggerConfig config = new SwaggerConfig();

        OpenAPI openAPI = config.customOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Reseña Service API", openAPI.getInfo().getTitle());
        assertEquals("1.0", openAPI.getInfo().getVersion());
        assertEquals("Documentación de la API para el sistema de reseñas", openAPI.getInfo().getDescription());
    }
}
