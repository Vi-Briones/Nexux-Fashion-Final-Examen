package com.Nexus_Fashion.resena_service.GlobalExceptionHandlerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.Nexus_Fashion.resena_service.exception.ApiErrorResponse;

public class ApiErrorResponseTest {

    @Test
    void testBuilderYGetters() {
        OffsetDateTime ahora = OffsetDateTime.now();

        ApiErrorResponse response = ApiErrorResponse.builder()
                .timestamp(ahora)
                .status(404)
                .error("Not Found")
                .message("Reseña no encontrada")
                .path("/resenas/1")
                .build();

        assertEquals(ahora, response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Reseña no encontrada", response.getMessage());
        assertEquals("/resenas/1", response.getPath());
    }

    @Test
    void testAllArgsConstructorYSetters() {
        OffsetDateTime ahora = OffsetDateTime.now();
        ApiErrorResponse response = new ApiErrorResponse(ahora, 400, "Bad Request", "Datos inválidos", "/resenas");

        response.setStatus(500);
        response.setError("Internal Server Error");
        response.setMessage("Error interno");
        response.setPath("/resenas/2");

        assertEquals(500, response.getStatus());
        assertEquals("Internal Server Error", response.getError());
        assertEquals("Error interno", response.getMessage());
        assertEquals("/resenas/2", response.getPath());
    }
}
