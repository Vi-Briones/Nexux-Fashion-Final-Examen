package com.Nexus_Fashion.compra_service.Compraglobalexceptionhandlertest;

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
import org.springframework.http.ResponseEntity;

import com.Nexus_Fashion.compra_service.exception.ApiErrorResponse;
import com.Nexus_Fashion.compra_service.exception.BadRequestException;
import com.Nexus_Fashion.compra_service.exception.GlobalExceptionHandler;
import com.Nexus_Fashion.compra_service.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/compras/test");
    }

    @Test
    void testHandleBadRequest_retorna400() {
        BadRequestException ex = new BadRequestException("Total inválido");

        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Total inválido", response.getBody().getMessage());
        assertEquals("/compras/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleBadRequest_conCausa_retorna400() {
        BadRequestException ex = new BadRequestException("Datos inválidos", new RuntimeException("causa"));

        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Datos inválidos", response.getBody().getMessage());
    }

    @Test
    void testHandleNotFound_retorna404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Compra no encontrada con ID: 99");

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Compra no encontrada con ID: 99", response.getBody().getMessage());
    }

    @Test
    void testHandleNotFound_conCausa_retorna404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("No existe", new RuntimeException("causa"));

        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No existe", response.getBody().getMessage());
    }

    @Test
    void testHandleGeneralException_retorna500() {
        Exception ex = new Exception("Error inesperado del sistema");

        ResponseEntity<ApiErrorResponse> response = handler.handleGeneralException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("error inesperado"));
    }
}