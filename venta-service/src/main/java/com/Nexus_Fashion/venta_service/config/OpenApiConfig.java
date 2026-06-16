package com.Nexus_Fashion.venta_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Venta Service API")
                        .version("1.0.0")
                        .description("API para la gestión de ventas en el sistema Nexus Fashion. Permite crear, actualizar, consultar y eliminar ventas, así como gestionar detalles de venta y métodos de pago.")
                        .contact(new Contact()
                                .name("Nexus Fashion")
                                .url("https://nexus-fashion.com")
                                .email("soporte@nexus-fashion.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addServersItem(new Server()
                        .url("http://localhost:9094")
                        .description("Servidor Local"))
                .addServersItem(new Server()
                        .url("http://api-gateway:8080")
                        .description("API Gateway"));
    }
}
