package com.Nexus_Fashion.compra_service.ConfigTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Nexus_Fashion.compra_service.config.SwaggerConfig;

import io.swagger.v3.oas.models.OpenAPI;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigTest {

    @InjectMocks
    private SwaggerConfig swaggerConfig;

    @Test
    void testCustomOpenAPISeCreaConMetadatosDeCompras() {
        OpenAPI resultado = swaggerConfig.customOpenAPI();

        assertNotNull(resultado);
        assertNotNull(resultado.getInfo());

        assertEquals("API 2026 Registro de Compras", resultado.getInfo().getTitle());
        assertEquals("1.0", resultado.getInfo().getVersion());
        assertEquals("Documentación de la API para el sistema de compras", resultado.getInfo().getDescription());
    }
}
