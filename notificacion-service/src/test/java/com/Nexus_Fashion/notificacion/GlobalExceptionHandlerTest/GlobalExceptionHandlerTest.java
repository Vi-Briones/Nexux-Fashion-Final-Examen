package com.Nexus_Fashion.notificacion.GlobalExceptionHandlerTest;

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

import com.Nexus_Fashion.notificacion_service.exepcion.ApiErrorResponse;
import com.Nexus_Fashion.notificacion_service.exepcion.BadRequestException;
import com.Nexus_Fashion.notificacion_service.exepcion.GlobalExceptionHandler;
import com.Nexus_Fashion.notificacion_service.exepcion.ResourceNotFoundException;
 

 
@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;
 
    @Mock
    private HttpServletRequest request;
 
    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/notificaciones/test");
    }
 
    @Test
    void testHandleNotFound_retorna404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Registro de notificación no encontrado");
 
        ResponseEntity<ApiErrorResponse> response = handler.handleNotFound(ex, request);
 
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Registro de notificación no encontrado", response.getBody().getMessage());
        assertEquals("/notificaciones/test", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }
 
    @Test
    void testHandleBadRequest_retorna400() {
        BadRequestException ex = new BadRequestException("SKU inválido");
 
        ResponseEntity<ApiErrorResponse> response = handler.handleBadRequest(ex, request);
 
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("SKU inválido", response.getBody().getMessage());
        assertEquals("/inventarios/test", response.getBody().getPath());
    }
 
    @Test
    void testHandleGeneral_retorna500() {
        Exception ex = new Exception("Error inesperado del sistema");
 
        ResponseEntity<ApiErrorResponse> response = handler.handleGeneral(ex, request);
 
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("Ha ocurrido un error interno", response.getBody().getMessage());
        assertEquals("/notificaciones/test", response.getBody().getPath());
    }
 
    @Test
    void testHandleGeneral_conIllegalArgumentException_retorna500() {
        IllegalArgumentException ex = new IllegalArgumentException("La cantidad disponible no puede ser menor a cero");
 
        ResponseEntity<ApiErrorResponse> response = handler.handleGeneral(ex, request);
 
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ha ocurrido un error interno", response.getBody().getMessage());
    }
}
