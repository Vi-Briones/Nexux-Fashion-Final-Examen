package com.Nexus_Fashion.recomendaciones_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.Nexus_Fashion.recomendaciones_service.exception.BadRequestException;
import com.Nexus_Fashion.recomendaciones_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;
import com.Nexus_Fashion.recomendaciones_service.repository.RecomendacionRepository;

@Service
public class RecomendacionService {
    private static final Logger logger = LoggerFactory.getLogger(RecomendacionService.class);

    private final RecomendacionRepository recomendacionRepository;
    private final WebClient webClient;

    @Value("${api.cliente.exists}")
    private String clientePath;

    @Value("${api.producto.exists}")
    private String productoPath;

    public RecomendacionService(RecomendacionRepository recomendacionRepository,
            @Value("${api.base-url}") String apiBaseUrl) {
        this.recomendacionRepository = recomendacionRepository;
        this.webClient = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .build();
    }

    public Recomendacion guardar(Recomendacion recomendacion) {
        logger.info("Iniciando proceso de guardado de recomendacion: idCliente={}, idProducto={}",
                recomendacion.getIdCliente(), recomendacion.getIdProducto());
        Boolean existeCliente;
        Boolean existeProducto;
        try {
            logger.debug("Validando existencia de cliente id={}", recomendacion.getIdCliente());
            existeCliente = webClient.get()
                    .uri(String.format(clientePath, recomendacion.getIdCliente()))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            logger.debug("Respuesta existencia cliente: {}", existeCliente);
        } catch (Exception e) {
            logger.error("Error al validar cliente id={}", recomendacion.getIdCliente(), e);
            throw new BadRequestException("Error al validar cliente");
        }
        try {
            Long idProducto =  recomendacion.getIdProducto();
            logger.debug("Validando existencia de producto id={}", idProducto);
            existeProducto = webClient.get()
                    .uri(String.format(productoPath, idProducto))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            logger.debug("Respuesta existencia producto: {}", existeProducto);
        } catch (Exception e) {
            logger.error("Error al validar producto id={}", recomendacion.getIdProducto(), e);
            throw new BadRequestException("Error al validar producto");
        }
        // Validaciones de negocio
        if (existeCliente == null) {
            logger.warn("Respuesta nula al validar cliente id={}", recomendacion.getIdCliente());
            throw new BadRequestException("No se pudo validar la existencia del cliente");
        }
        if (Boolean.FALSE.equals(existeCliente)) {
            logger.warn("Cliente no existe id={}", recomendacion.getIdCliente());
            throw new ResourceNotFoundException("Cliente no existe");
        }
        if (existeProducto == null) {
            logger.warn("Respuesta nula al validar producto id={}", recomendacion.getIdProducto());
            throw new BadRequestException("No se pudo validar la existencia del producto");
        }
        if (Boolean.FALSE.equals(existeProducto)) {
            logger.warn("Producto no existe id={}", recomendacion.getIdProducto());
            throw new ResourceNotFoundException("Producto no existe");
        }
        Recomendacion recomendacionGuardar = recomendacionRepository.save(recomendacion);
        logger.info("Recomendacion guardada exitosamente con id={}", recomendacionGuardar.getId());
        return recomendacionGuardar;
    }

    public List<Recomendacion> listar() {
        logger.info("Listando todas las recomendaciones");
        List<Recomendacion> recomendaciones = recomendacionRepository.findAll();
        logger.debug("Cantidad de recomendaciones encontradas: {}", recomendaciones.size());
        return recomendaciones;
    }

    public Recomendacion buscarPorId(Long id) {
        logger.info("Buscando recomendacion con ID={}", id);
        Recomendacion recomendacion = recomendacionRepository.findById(id).orElse(null);
        if (recomendacion == null) {
            logger.warn("No se encontró recomendacion con ID={}", id);
        } else {
            logger.info("Recomendacion encontrada con ID={}", id);
        }
        return recomendacion;
    }

    public List<Recomendacion> listarPorCliente(Long idCliente) {
        logger.info("Listando recomendaciones para cliente ID={}", idCliente);
        List<Recomendacion> recomendaciones = recomendacionRepository.findByIdCliente(idCliente);
        logger.debug("Cantidad de recomendaciones para cliente {}: {}", idCliente, recomendaciones.size());
        return recomendaciones;
    }

    public boolean existePorId(Long id) {
        logger.info("Validando existencia de recomendacion con ID={}", id);
        boolean existe = recomendacionRepository.existsById(id);
        if (existe) {
            logger.info("Recomendacion ID={} existe y está validada", id);
        } else {
            logger.warn("Recomendacion ID={} NO existe en la base de datos", id);
        }
        return existe;
    }

    public Recomendacion actualizar(Long id, Recomendacion recomendacionActualizada) {
    logger.info("Actualizando recomendacion con ID={}", id);

    Recomendacion recomendacion = recomendacionRepository.findById(id).orElse(null);
    if (recomendacion == null) {
        logger.warn("No existe recomendacion con ID={} para actualizar", id);
        throw new ResourceNotFoundException("Recomendacion no encontrada");
    }

    recomendacion.setTipoRecomendacion(recomendacionActualizada.getTipoRecomendacion());
    recomendacion.setComentario(recomendacionActualizada.getComentario());
    recomendacion.setPuntajeAfinidad(recomendacionActualizada.getPuntajeAfinidad());

    Recomendacion actualizada = recomendacionRepository.save(recomendacion);
    logger.info("Recomendacion ID={} actualizada exitosamente", id);
    
    return actualizada;
}

    public void eliminar(Long id) {
        logger.info("Eliminando recomendacion con ID={}", id);
        
        if (!recomendacionRepository.existsById(id)) {
            logger.warn("No existe recomendacion con ID={} para eliminar", id);
            throw new ResourceNotFoundException("Recomendacion no encontrada");
        }

        recomendacionRepository.deleteById(id);
        logger.info("Recomendacion ID={} eliminada exitosamente", id);
    }
}
