package com.Nexus_Fashion.cliente_service.service;

import org.springframework.stereotype.Service;
import com.Nexus_Fashion.cliente_service.model.Cliente;
import com.Nexus_Fashion.cliente_service.repository.ClienteRepository;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository; 
    }

    public Cliente guardar(Cliente cliente) {
        logger.info("Servicio Cliente: Iniciando proceso para registrar al cliente con correo [{}]", cliente.getCorreo());
        
        // Mantenemos la validación original de tu compañero
        if (cliente.getIdCliente() == null && clienteRepository.existsByCorreo(cliente.getCorreo())) {
            logger.warn("Servicio Cliente - ADVERTENCIA: Intento de registro fallido. El correo [{}] ya existe en la base de datos.", cliente.getCorreo());
            throw new RuntimeException("Error: El correo " + cliente.getCorreo() + " ya está registrado.");
        }
        
        try {
            Cliente clienteGuardado = clienteRepository.save(cliente);
            logger.info("Servicio Cliente: Cliente guardado exitosamente en la base de datos con ID={}", clienteGuardado.getIdCliente());
            return clienteGuardado;
        } catch (Exception e) {
            logger.error("Servicio Cliente - ERROR CRÍTICO: No se pudo persistir el cliente en la base de datos", e);
            throw e;
        }
    }

    public boolean existePorId(Long id) {
        logger.info("Servicio Cliente: Evaluando existencia en la base de datos para el ID={}", id);
        
        boolean existe = clienteRepository.existsById(id);
        
        if (existe) {
            logger.info("Servicio Cliente: Confirmado. El ID={} SÍ existe.", id);
        } else {
            logger.warn("Servicio Cliente - ADVERTENCIA: El ID={} NO corresponde a ningún cliente registrado.", id);
        }
        
        return existe;
    }

    public List<Cliente> listar() {
        logger.info("Servicio Cliente: Solicitando consulta de todos los clientes a la base de datos");
        List<Cliente> lista = clienteRepository.findAll();
        logger.info("Servicio Cliente: Consulta finalizada. Se recuperaron {} registros.", lista.size());
        return lista;
    }

    public Cliente buscarPorId(Long id) {
        logger.info("Servicio Cliente: Buscando cliente con ID={}", id);
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) {
            logger.warn("Servicio Cliente - ADVERTENCIA: No se encontró cliente con ID={}", id);
        } else {
            logger.info("Servicio Cliente: Cliente encontrado con ID={}", id);
        }
        return cliente;
    }

    public Cliente actualizar(Long id, Cliente clienteActualizado) {
        logger.info("Servicio Cliente: Iniciando actualización del cliente con ID={}", id);
        
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) {
            logger.warn("Servicio Cliente - ERROR: No existe cliente con ID={} para actualizar", id);
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }

        cliente.setNombre(clienteActualizado.getNombre());
        cliente.setCorreo(clienteActualizado.getCorreo());
        if (clienteActualizado.getContrasena() != null && !clienteActualizado.getContrasena().isEmpty()) {
            cliente.setContrasena(clienteActualizado.getContrasena());
        }
        if (clienteActualizado.getRol() != null) {
            cliente.setRol(clienteActualizado.getRol());
        }

        Cliente actualizado = clienteRepository.save(cliente);
        logger.info("Servicio Cliente: Cliente ID={} actualizado exitosamente", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("Servicio Cliente: Iniciando eliminación del cliente con ID={}", id);
        
        if (!clienteRepository.existsById(id)) {
            logger.warn("Servicio Cliente - ERROR: No existe cliente con ID={} para eliminar", id);
            throw new RuntimeException("Cliente no encontrado con ID: " + id);
        }

        clienteRepository.deleteById(id);
        logger.info("Servicio Cliente: Cliente ID={} eliminado exitosamente", id);
    }
}