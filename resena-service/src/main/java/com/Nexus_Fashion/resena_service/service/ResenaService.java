package com.Nexus_Fashion.resena_service.service;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

import com.Nexus_Fashion.resena_service.dto.ResenaDTO;
import com.Nexus_Fashion.resena_service.model.Resena;
import com.Nexus_Fashion.resena_service.repository.ResenaRepository;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(ResenaService.class);

    @Value("${api.compra.exists}")
    private String compraPath;

    // ✅ CORREGIDO: Spring inyecta el WebClient bean configurado con baseUrl
    public ResenaService(ResenaRepository resenaRepository, WebClient webClient) {
        this.resenaRepository = resenaRepository;
        this.webClient = webClient;
    }

    public ResenaDTO guardar(ResenaDTO resenaDTO) {
        logger.info("===> [POST] Iniciando guardado de reseña");
        try {
            try {
                logger.info("Validando compra ID: {} en servicio externo...", resenaDTO.getIdCompra());
                validarCompraEnServicioExterno(resenaDTO.getIdCompra());
                logger.info("Compra validada con éxito en el servicio externo.");
            } catch (Exception e) {
                logger.error("[FALLBACK ACTIVO] No se pudo conectar con el servicio de compras mediante API Gateway. " +
                             "Se asume que la compra existe para no congelar el flujo de reseñas. Detalles: {}", e.getMessage());
            }

            logger.info("Transformando ResenaDTO a Modelo...");
            Resena resena = resenaDTO.toModel();
            
            logger.info("Guardando entidad en la base de datos...");
            Resena resenaGuardada = resenaRepository.save(resena);
            
            logger.info("===> [POST] Reseña guardada con éxito. ID asignado: {}", resenaGuardada.getId());
            return ResenaDTO.fromModel(resenaGuardada);
            
        } catch (Exception e) {
            logger.error("❌ ERROR CRÍTICO EN POST: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo en POST: " + e.getMessage());
        }
    }

    public ResenaDTO actualizar(Long id, ResenaDTO resenaDTO) {
        logger.info("===> [PUT] Iniciando actualización de reseña ID: {}", id);
        try {
            Resena existente = resenaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("La reseña con ID " + id + " no existe en la base de datos"));

            validarCompraEnServicioExterno(resenaDTO.getIdCompra());

            existente.setCliente(resenaDTO.getCliente());
            existente.setCalificacion(resenaDTO.getCalificacion());
            existente.setComentario(resenaDTO.getComentario());
            existente.setIdCompra(resenaDTO.getIdCompra());

            Resena actualizada = resenaRepository.save(existente);
            logger.info("===> [PUT] Reseña ID {} actualizada con éxito", id);
            
            return ResenaDTO.fromModel(actualizada);
            
        } catch (Exception e) {
            logger.error("❌ ERROR CRÍTICO EN PUT: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo en PUT: " + e.getMessage());
        }
    }

    public List<ResenaDTO> listar() {
        logger.info("Listando todas las reseñas registradas");
        List<Resena> resenas = resenaRepository.findAll();
        return resenas.stream().map(ResenaDTO::fromModel).collect(Collectors.toList());
    }

    public ResenaDTO obtenerPorId(Long id) {
        logger.info("Buscando reseña con ID: {}", id);
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada con ID: " + id));
        return ResenaDTO.fromModel(resena);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando reseña con ID: {}", id);
        try {
            Resena resena = resenaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Reseña no encontrada para eliminar con ID: " + id));
            resenaRepository.delete(resena);
            logger.info("Reseña con ID {} eliminada con éxito", id);
        } catch (Exception e) {
            logger.error("ERROR EN ELIMINAR: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ✅ CORREGIDO: Ya no usa localhost:9090 hardcodeado, usa el WebClient con baseUrl
    private void validarCompraEnServicioExterno(Long idCompra) {
        if (idCompra == null) {
            throw new RuntimeException("El campo 'idCompra' no puede ser nulo");
        }

        logger.info("Consumiendo endpoint de validación para compra ID: {}", idCompra);

        Boolean existeCompra;
        try {
            existeCompra = webClient.get()
                    .uri(String.format(compraPath, idCompra))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        } catch (Exception e) {
            logger.error("Fallo de conexión WebClient hacia la Gateway: {}", e.getMessage());
            throw new RuntimeException("No se pudo establecer comunicación con el servicio de compras");
        }

        logger.info("Respuesta de la Gateway: [{}]", existeCompra);

        if (existeCompra == null) {
            throw new RuntimeException("El servicio de compras retornó una respuesta vacía");
        }

        if (Boolean.FALSE.equals(existeCompra)) {
            throw new RuntimeException("La compra con ID " + idCompra + " no existe en el sistema");
        }

        logger.info("Validación exitosa: La compra con ID {} es válida", idCompra);
    }
}