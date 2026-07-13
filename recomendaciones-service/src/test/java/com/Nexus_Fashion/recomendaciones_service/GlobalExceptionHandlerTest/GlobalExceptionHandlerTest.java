package com.Nexus_Fashion.recomendaciones_service.GlobalExceptionHandlerTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.Nexus_Fashion.recomendaciones_service.exception.ApiErrorResponse;
import com.Nexus_Fashion.recomendaciones_service.exception.BadRequestException;
import com.Nexus_Fashion.recomendaciones_service.exception.GlobalExceptionHandler;
import com.Nexus_Fashion.recomendaciones_service.exception.ResourceNotFoundException;

import java.net.URI;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/recomendaciones/test");
    }

    // ─────────────────────────── BadRequest 400 ────────────────────

    @Test
    void testHandleBadRequest_retorna400() {
        BadRequestException ex = new BadRequestException("Error al validar cliente");

        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Error al validar cliente", response.getBody().getMessage());
        assertEquals("/recomendaciones/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleBadRequest_conMensajeProducto_retorna400() {
        BadRequestException ex = new BadRequestException("Error al validar producto");

        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error al validar producto", response.getBody().getMessage());
    }

    // ─────────────────────────── NotFound 404 ──────────────────────

    @Test
    void testHandleNotFound_retorna404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recomendacion no encontrada");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Recomendacion no encontrada", response.getBody().getMessage());
        assertEquals("/recomendaciones/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleNotFound_clienteNoExiste_retorna404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Cliente no existe");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Cliente no existe", response.getBody().getMessage());
    }

    // ──────────────────── WebClientResponse 503 ────────────────────

    @Test
    void testHandleWebClientResponse_retornaStatusDelError() {
        WebClientResponseException ex = WebClientResponseException.create(
                503, "Service Unavailable", null, null, null);

        ResponseEntity<ApiErrorResponse> response = handler.handleWebClientResponse(ex, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Error de comunicación"));
        assertEquals("/recomendaciones/test", response.getBody().getPath());
    }

    // ──────────────────── WebClientRequest 502 ─────────────────────

    @Test
    void testHandleWebClientRequest_retorna502() {
        WebClientRequestException ex = new WebClientRequestException(
                new RuntimeException("connection refused"),
                HttpMethod.GET,
                URI.create("http://cliente-service/api/clientes/exists/1"),
                HttpHeaders.EMPTY
        );

        ResponseEntity<ApiErrorResponse> response = handler.handleWebClientRequest(ex, request);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertEquals(502, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("No se pudo establecer conexión"));
        assertEquals("/recomendaciones/test", response.getBody().getPath());
    }

    // ─────────────────────── GeneralException 500 ──────────────────

    @Test
    void testHandleGeneralException_retorna500() {
        Exception ex = new Exception("Error inesperado del sistema");

        ResponseEntity<ApiErrorResponse> response = handler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Ocurrió un error inesperado"));
        assertEquals("/recomendaciones/test", response.getBody().getPath());
    }

    @Test
    void testHandleGeneralException_conIllegalArgument_retorna500() {
        IllegalArgumentException ex = new IllegalArgumentException("Puntaje fuera de rango");

        ResponseEntity<ApiErrorResponse> response = handler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().getMessage().contains("Ocurrió un error inesperado"));
    }
}