package com.Nexus_Fashion.cliente_service.ClienteServiceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.Nexus_Fashion.cliente_service.model.Cliente;
import com.Nexus_Fashion.cliente_service.model.Rol;
import com.Nexus_Fashion.cliente_service.repository.ClienteRepository;
import com.Nexus_Fashion.cliente_service.service.ClienteService;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;
 
    @InjectMocks
    private ClienteService clienteService;
 
    private Rol rol;
    private Cliente clienteEjemplo;
 
    @BeforeEach
    void setUp() {
        rol = new Rol(1L, "CLIENTE");
 
        clienteEjemplo = new Cliente(
                1L,
                "Juan Pérez",
                "juan@correo.com",
                "password123",
                rol
        );
    }
 
    @Test
    void testGuardar_exitoso() {
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEjemplo);

        Cliente resultado = clienteService.guardar(clienteEjemplo);

        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("juan@correo.com", resultado.getCorreo());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }
 
    @Test
    void testGuardar_correoYaRegistrado_lanzaExcepcion() {
        // idCliente null => cliente nuevo, correo ya existe => debe rechazarse
        Cliente clienteNuevo = new Cliente(null, "Ana López", "juan@correo.com", "pass123", rol);
        when(clienteRepository.existsByCorreo("juan@correo.com")).thenReturn(true);
 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.guardar(clienteNuevo));
 
        assertTrue(ex.getMessage().contains("ya está registrado"));
        verify(clienteRepository, never()).save(any());
    }
 
    @Test
    void testGuardar_actualizacion_noValidaCorreoDuplicado() {
        // idCliente no null => es actualización, no valida correo duplicado
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEjemplo);
 
        Cliente resultado = clienteService.guardar(clienteEjemplo);
 
        assertNotNull(resultado);
        verify(clienteRepository, never()).existsByCorreo(any());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testExistePorId_existe_retornaTrue() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
 
        boolean resultado = clienteService.existePorId(1L);
 
        assertTrue(resultado);
        verify(clienteRepository, times(1)).existsById(1L);
    }
 
    @Test
    void testExistePorId_noExiste_retornaFalse() {
        when(clienteRepository.existsById(99L)).thenReturn(false);
 
        boolean resultado = clienteService.existePorId(99L);
 
        assertFalse(resultado);
        verify(clienteRepository, times(1)).existsById(99L);
    }

    @Test
    void testListar_retornaLista() {
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(clienteEjemplo));
 
        List<Cliente> resultado = clienteService.listar();
 
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        verify(clienteRepository, times(1)).findAll();
    }
 
    @Test
    void testListar_listaVacia() {
        when(clienteRepository.findAll()).thenReturn(Collections.emptyList());
 
        List<Cliente> resultado = clienteService.listar();
 
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId_encontrado() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEjemplo));
 
        Cliente resultado = clienteService.buscarPorId(1L);
 
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCliente());
        assertEquals("Juan Pérez", resultado.getNombre());
        verify(clienteRepository, times(1)).findById(1L);
    }
 
    @Test
    void testBuscarPorId_noEncontrado_retornaNull() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
 
        Cliente resultado = clienteService.buscarPorId(99L);
 
        assertNull(resultado);
        verify(clienteRepository, times(1)).findById(99L);
    }

    @Test
    void testActualizar_exitoso() {
        Cliente clienteActualizado = new Cliente(1L, "Juan Modificado", "nuevo@correo.com", "newpass", rol);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEjemplo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteActualizado);
 
        Cliente resultado = clienteService.actualizar(1L, clienteActualizado);
 
        assertNotNull(resultado);
        assertEquals("Juan Modificado", resultado.getNombre());
        assertEquals("nuevo@correo.com", resultado.getCorreo());
        verify(clienteRepository, times(1)).findById(1L);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }
 
    @Test
    void testActualizar_noExiste_lanzaExcepcion() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
 
        Cliente clienteActualizado = new Cliente(99L, "Nadie", "nadie@correo.com", "pass", rol);
 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.actualizar(99L, clienteActualizado));
 
        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
        verify(clienteRepository, never()).save(any());
    }
 
    @Test
    void testActualizar_sinContrasena_noActualizaContrasena() {
        // contrasena null => no debe actualizarse
        Cliente sinContrasena = new Cliente(1L, "Juan Modificado", "nuevo@correo.com", null, rol);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEjemplo));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEjemplo);
 
        Cliente resultado = clienteService.actualizar(1L, sinContrasena);
 
        assertNotNull(resultado);
        // la contraseña original se mantiene
        assertEquals("password123", resultado.getContrasena());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void testEliminar_exitoso() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);
 
        clienteService.eliminar(1L);
 
        verify(clienteRepository, times(1)).deleteById(1L);
    }
 
    @Test
    void testEliminar_noExiste_lanzaExcepcion() {
        when(clienteRepository.existsById(99L)).thenReturn(false);
 
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> clienteService.eliminar(99L));
 
        assertTrue(ex.getMessage().contains("Cliente no encontrado"));
        verify(clienteRepository, never()).deleteById(any());
    }
}
