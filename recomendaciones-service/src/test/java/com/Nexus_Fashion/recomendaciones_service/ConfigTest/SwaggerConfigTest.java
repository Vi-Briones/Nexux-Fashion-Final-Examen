package com.Nexus_Fashion.recomendaciones_service.ConfigTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Nexus_Fashion.recomendaciones_service.config.SwaggerConfig;

import io.swagger.v3.oas.models.OpenAPI;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigTest {

    @InjectMocks
    private SwaggerConfig swaggerConfig;

    @Test
    void testCustomOpenAPISeCreaConMetadatosCorrectos() {

        OpenAPI resultado = swaggerConfig.customOpenAPI();


        assertNotNull(resultado, "El bean OpenAPI no debería ser nulo");
        assertNotNull(resultado.getInfo(), "El objeto Info dentro de OpenAPI no debería ser nulo");
        
        assertEquals("API 2026 Registro de recomendaciones", resultado.getInfo().getTitle());
        assertEquals("1.0", resultado.getInfo().getVersion());
        assertEquals("Documentación de la API para el sistema de recomendaciones", resultado.getInfo().getDescription());
    }
}