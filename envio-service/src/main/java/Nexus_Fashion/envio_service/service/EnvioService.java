package Nexus_Fashion.envio_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import Nexus_Fashion.envio_service.model.Envio;
import Nexus_Fashion.envio_service.repository.EnvioRepository;

import java.util.List;

@Service
public class EnvioService {

    private static final Logger logger = LoggerFactory.getLogger(EnvioService.class);
    
    private final EnvioRepository envioRepository;
    private final WebClient webClient;

    // Ruta inyectada desde application.properties que apunta al Gateway (puerto 9090)
    @Value("${api.compra.exists}")
    private String compraPath;

    public EnvioService(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
        this.webClient = WebClient.create();
    }

    /**
     * LÓGICA DE ESCRITURA (POST)
     * Valida la compra usando WebClient antes de guardar el envío en las dos tablas.
     */
    public Envio guardar(Envio envio) {
        logger.info("Aduana de Envíos: Verificando validez de la compra ID={} con el microservicio de Compras", envio.getIdCompra());
        
        Boolean existeCompra;
        try {
            // Consumo síncrono por WebClient apuntando al ecosistema unificado
            existeCompra = webClient.get()
                    .uri(String.format(compraPath, envio.getIdCompra()))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block(); // Esperamos la respuesta booleana real
                    
        } catch (Exception e) {
            logger.error("Aduana de Envíos - ERROR: Falló la comunicación con compra-service para ID={}", envio.getIdCompra(), e);
            throw new RuntimeException("Servicio de Compras no disponible temporalmente. Intente más tarde.");
        }

        // Si la compra no existe o devuelve nulo, frena la transacción
        if (Boolean.FALSE.equals(existeCompra) || existeCompra == null) {
            logger.warn("Aduana de Envíos - RECHAZADO: La compra ID={} no existe en el sistema Nexus Fashion", envio.getIdCompra());
            throw new RuntimeException("Operación inválida: La compra especificada no existe.");
        }

        logger.info("Aduana de Envíos - APROBADO: Compra existente. Insertando registros en 'envios' y 'detalles_envio'");
        
        // Gracias a CascadeType.ALL en el modelo Envio, guardar la cabecera guarda automáticamente el detalle
        return envioRepository.save(envio);
    }

    /**
     * LÓGICA DE LECTURA (GET ALL)
     * Recupera todos los registros de despachos guardados.
     */
    public List<Envio> obtenerTodos() {
        logger.info("Servicio Envíos: Buscando todos los registros en la base de datos");
        return envioRepository.findAll();
    }

    /**
     * LÓGICA DE LECTURA (GET BY ID)
     * Busca un despacho específico mediante su clave primaria.
     */
    public Envio buscarPorId(Long id) {
        logger.info("Servicio Envíos: Buscando registro con ID={}", id);
        return envioRepository.findById(id).orElse(null);
    }

    public Envio actualizar(Long id, Envio envioActualizado) {
        logger.info("Servicio Envíos: Actualizando envío con ID={}", id);
        
        Envio envio = envioRepository.findById(id).orElse(null);
        if (envio == null) {
            logger.warn("Servicio Envíos - ERROR: No existe envío con ID={}", id);
            throw new RuntimeException("Envío no encontrado");
        }

        envio.setEstadoEnvio(envioActualizado.getEstadoEnvio());
        envio.setFechaEnvio(envioActualizado.getFechaEnvio());
        
        Envio actualizado = envioRepository.save(envio);
        logger.info("Servicio Envíos: Envío ID={} actualizado exitosamente", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("Servicio Envíos: Eliminando envío con ID={}", id);
        
        if (!envioRepository.existsById(id)) {
            logger.warn("Servicio Envíos - ERROR: No existe envío con ID={}", id);
            throw new RuntimeException("Envío no encontrado");
        }

        envioRepository.deleteById(id);
        logger.info("Servicio Envíos: Envío ID={} eliminado exitosamente", id);
    }
}
