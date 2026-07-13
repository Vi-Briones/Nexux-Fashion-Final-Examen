package Nexus_Fashion.envio_service.ConfigTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import Nexus_Fashion.envio_service.config.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigTest {

    @InjectMocks
    private SwaggerConfig swaggerConfig;

    @Test
    void testCustomOpenAPISeCreaConMetadatosDeEnvios() {
        OpenAPI resultado = swaggerConfig.customOpenAPI();

        assertNotNull(resultado);
        assertNotNull(resultado.getInfo());
        
        assertEquals("API 2026 Registro de Envíos", resultado.getInfo().getTitle());
        assertEquals("1.0", resultado.getInfo().getVersion());
        assertEquals("Documentación de la API para el sistema de envíos", resultado.getInfo().getDescription());
    }
}