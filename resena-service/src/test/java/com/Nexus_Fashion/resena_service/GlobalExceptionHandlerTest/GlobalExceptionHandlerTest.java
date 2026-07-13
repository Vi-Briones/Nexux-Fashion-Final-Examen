package com.Nexus_Fashion.resena_service.GlobalExceptionHandlerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.Nexus_Fashion.resena_service.exception.ApiErrorResponse;
import com.Nexus_Fashion.resena_service.exception.BadRequestException;
import com.Nexus_Fashion.resena_service.exception.GlobalExceptionHandler;
import com.Nexus_Fashion.resena_service.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/resenas/test");
    }

    @Test
    void testHandleNotFound_retorna404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Reseña no encontrada con ID: 99");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Reseña no encontrada con ID: 99", response.getBody().getMessage());
        assertEquals("/resenas/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleBadRequest_retorna400() {
        BadRequestException ex = new BadRequestException("Calificación inválida");

        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("Calificación inválida", response.getBody().getMessage());
        assertEquals("/resenas/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleGeneral_retorna500() {
        Exception ex = new Exception("Error inesperado del sistema");

        ResponseEntity<ApiErrorResponse> response = handler.handleGeneral(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Ha ocurrido un error interno", response.getBody().getMessage());
        assertEquals("/resenas/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleGeneral_conRuntimeException_retorna500() {
        RuntimeException ex = new RuntimeException("Fallo en POST: la compra no existe");

        ResponseEntity<ApiErrorResponse> response = handler.handleGeneral(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ha ocurrido un error interno", response.getBody().getMessage());
    }

    @Test
    void testHandleGeneral_conNullPointerException_retorna500() {
        NullPointerException ex = new NullPointerException("Campo nulo inesperado");

        ResponseEntity<ApiErrorResponse> response = handler.handleGeneral(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
    }
}
