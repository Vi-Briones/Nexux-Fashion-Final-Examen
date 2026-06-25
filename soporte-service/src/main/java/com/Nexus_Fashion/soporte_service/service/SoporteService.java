package com.Nexus_Fashion.soporte_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.Nexus_Fashion.soporte_service.model.Soporte;
import com.Nexus_Fashion.soporte_service.repository.SoporteRepository;

import org.springframework.beans.factory.annotation.Value;

@Service
public class SoporteService {

    private final SoporteRepository soporteRepository;
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(SoporteService.class);

    @Value("${api.cliente.exists}")
    private String clientePath;

    public SoporteService(SoporteRepository soporteRepository, WebClient webClient) {
        this.soporteRepository = soporteRepository;
        this.webClient = webClient;
    }

    public Soporte guardar(Soporte soporte) {
        logger.info("Iniciando guardar ticket de soporte con idCliente={}, asunto={}, prioridad={}", 
                soporte.getIdCliente(), soporte.getAsunto(), soporte.getPrioridad());
        try {
            // 1. Validaciones iniciales de los datos de entrada
            if (soporte.getIdCliente() == null || soporte.getIdCliente() <= 0) {
                throw new IllegalArgumentException("idCliente (Cliente) requerido y debe ser mayor a 0");
            }
            if (soporte.getAsunto() == null || soporte.getAsunto().isBlank()) {
                throw new IllegalArgumentException("asunto requerido");
            }
            if (soporte.getDescripcion() == null || soporte.getDescripcion().isBlank()) {
                throw new IllegalArgumentException("descripción requerida");
            }
            
            // 2. Asignación de valores por defecto si vienen vacíos
            if (soporte.getEstado() == null || soporte.getEstado().isBlank()) {
                soporte.setEstado("PENDIENTE");
            }
            if (soporte.getPrioridad() == null || soporte.getPrioridad().isBlank()) {
                soporte.setPrioridad("MEDIA");
            }
            if (soporte.getFechaCreacion() == null) {
                soporte.setFechaCreacion(LocalDateTime.now());
            }

            // 3. Intento de validación externa con Tolerancia a Fallos (Fallback)
            Boolean existeCliente;
            try {
                String uri = String.format(clientePath, soporte.getIdCliente());
                logger.info("URI = {}", uri);
                logger.info("Realizando petición a api-gateway/clientes: {}", uri);
                
                existeCliente = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .block();
                
                logger.info("Respuesta de api-gateway: existeCliente={}", existeCliente);
            } catch (Exception e) {
                // FALLBACK: Si falla la red de Docker o el microservicio de clientes está abajo,
                // atrapamos la excepción aquí para evitar el Error 500 y dejamos pasar la petición.
                logger.error("[FALLBACK ACTIVO] No se pudo conectar con el servicio de clientes (Error de comunicación). " +
                             "Se asume que el cliente existe para no detener el flujo. Detalles: {}", e.getMessage());
                existeCliente = true; 
            }

            // 4. Procesar el resultado de la validación
            if (existeCliente == null) {
                logger.error("No se pudo validar la existencia del cliente (El servicio retornó null)");
                throw new RuntimeException("No se pudo validar la existencia del cliente"); 
            }
            if (Boolean.FALSE.equals(existeCliente)) {
                logger.warn("Cliente no existe con id={}", soporte.getIdCliente());
                throw new RuntimeException("Cliente no existe");
            }

            // 5. Persistencia en la Base de Datos
            Soporte ticketGuardado = soporteRepository.save(soporte);
            logger.info("Ticket de soporte guardado exitosamente con id={}", ticketGuardado.getId());
            return ticketGuardado;
            
        } catch (Exception e) {
            logger.error("Error crítico al guardar ticket de soporte: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Soporte> listar() {
        logger.info("Listando todos los tickets de soporte");
        List<Soporte> lista = soporteRepository.findAll();
        logger.info("Total tickets encontrados: {}", lista.size());
        return lista;
    }

    public Soporte obtenerPorId(Long id) {
        logger.info("Buscando ticket de soporte por id={}", id);
        Soporte soporte = soporteRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Ticket de soporte no encontrado id={}", id);
                    return new RuntimeException("Ticket de soporte no existe");
                });
        logger.info("Ticket de soporte encontrado id={}", id);
        return soporte;
    }

    public Soporte actualizar(Long id, Soporte soporte) {
        logger.info("Iniciando actualizar ticket de soporte id={}", id);
        try {
            Soporte existente = soporteRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ticket de soporte no existe"));

            if (soporte.getAsunto() == null || soporte.getAsunto().isBlank()) {
                throw new IllegalArgumentException("asunto requerido");
            }
            if (soporte.getDescripcion() == null || soporte.getDescripcion().isBlank()) {
                throw new IllegalArgumentException("descripción requerida");
            }
            if (soporte.getEstado() == null || soporte.getEstado().isBlank()) {
                throw new IllegalArgumentException("estado requerido");
            }

            String uri = String.format(clientePath, soporte.getIdCliente());
            logger.info("Validando existencia de cliente para soporte idCliente={}", soporte.getIdCliente());
            
            Boolean existeCliente = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            logger.info("Respuesta de validación cliente: {}", existeCliente);

            if (existeCliente == null) {
                logger.error("No se pudo validar la existencia del cliente");
                throw new RuntimeException("No se pudo validar la existencia del cliente");
            }
            if (Boolean.FALSE.equals(existeCliente)) {
                logger.warn("Cliente no existe con id={}", soporte.getIdCliente());
                throw new RuntimeException("Cliente no existe");
            }

            existente.setIdCliente(soporte.getIdCliente());
            existente.setAsunto(soporte.getAsunto());
            existente.setDescripcion(soporte.getDescripcion());
            existente.setEstado(soporte.getEstado());
            existente.setPrioridad(soporte.getPrioridad());
            existente.setFechaActualizacion(LocalDateTime.now());

            Soporte actualizado = soporteRepository.save(existente);
            logger.info("Ticket de soporte actualizado exitosamente id={}", actualizado.getId());
            return actualizado;
        } catch (Exception e) {
            logger.error("Error al actualizar ticket de soporte id={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void eliminar(Long id) {
        logger.info("Iniciando eliminación de ticket de soporte id={}", id);
        try {
            if (!soporteRepository.existsById(id)) {
                logger.warn("Ticket de soporte no existe para eliminar id={}", id);
                throw new RuntimeException("Ticket de soporte no existe");
            }
            soporteRepository.deleteById(id);
            logger.info("Ticket de soporte eliminado exitosamente id={}", id);
        } catch (Exception e) {
            logger.error("Error al eliminar ticket de soporte id={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

}
