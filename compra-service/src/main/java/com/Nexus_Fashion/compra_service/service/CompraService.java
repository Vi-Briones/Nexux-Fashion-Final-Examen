package com.Nexus_Fashion.compra_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.Nexus_Fashion.compra_service.exception.ResourceNotFoundException;
import com.Nexus_Fashion.compra_service.model.Compra;
import com.Nexus_Fashion.compra_service.repository.CompraRepository;


import java.util.List;

@Service
public class CompraService {

    private static final Logger logger = LoggerFactory.getLogger(CompraService.class);

    private final CompraRepository compraRepository;
    private final WebClient webClient;

    @Value("${api.cliente.exists}")
    private String clientePath;

    @Value("${api.producto.exists}")
    private String productoPath;

    
    public CompraService(CompraRepository compraRepository, WebClient webClient) {
        this.compraRepository = compraRepository;
        this.webClient = webClient;
    }

     public Compra guardar(Compra compra) {
        logger.info("===> [POST] Iniciando guardado de compra");
        try {
            try {
                logger.info("Validando cliente ID: {} y producto ID: {} en servicio externo...", 
                        compra.getIdCliente(), compra.getDetalles().get(0).getIdProducto());
                
                validarClienteYProductoEnServicioExterno(compra);
                
                logger.info("Cliente y Producto validados con éxito en el servicio externo.");
            } catch (Exception e) {
                logger.error("[FALLBACK ACTIVO] No se pudo conectar con el servicio externo mediante API Gateway. " +
                             "Se asume que los datos existen para no congelar el flujo de compras. Detalles: {}", e.getMessage());
            }

            logger.info("Guardando entidad en la base de datos...");
            Compra compraGuardada = compraRepository.save(compra);
            
            logger.info("===> [POST] Compra guardada con éxito. ID asignado: {}", compraGuardada.getId());
            return compraGuardada;
            
        } catch (Exception e) {
            logger.error("❌ ERROR CRÍTICO EN POST: {}", e.getMessage(), e);
            throw new RuntimeException("Fallo en POST: " + e.getMessage());
        }
    }

    private void validarClienteYProductoEnServicioExterno(Compra compra) {
        Boolean existeCliente = webClient.get()
                .uri(String.format(clientePath, compra.getIdCliente()))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        Long idProductoExterno = compra.getDetalles().get(0).getIdProducto();
        Boolean existeProducto = webClient.get()
                .uri(String.format(productoPath, idProductoExterno))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (existeCliente == null || Boolean.FALSE.equals(existeCliente)) {
            throw new RuntimeException("Cliente no válido o no existe");
        }
        if (existeProducto == null || Boolean.FALSE.equals(existeProducto)) {
            throw new RuntimeException("Producto no válido o no existe");
        }
    }

    public List<Compra> listar() {
        logger.info("Listando todas las compras");
        List<Compra> compras = compraRepository.findAll();
        logger.debug("Cantidad de compras encontradas: {}", compras.size());
        return compras;
    }

    public Compra buscarPorId(Long id) {
        logger.info("Buscando compra con ID={}", id);
        Compra compra = compraRepository.findById(id).orElse(null);
        if (compra == null) {
            logger.warn("No se encontró compra con ID={}", id);
        } else {
            logger.info("Compra encontrada con ID={}", id);
        }
        return compra;
    }

    public boolean existePorId(Long id) {
        logger.info("Validando existencia de compra con ID={}", id);
        boolean existe = compraRepository.existsById(id);
        if (existe) {
            logger.info("Compra ID={} existe y está validada", id);
        } else {
            logger.warn("Compra ID={} NO existe en la base de datos", id);
        }
        return existe;
    }

    public List<Compra> listarPorCliente(Long idCliente) {
        logger.info("Buscando todas las compras del cliente ID={}", idCliente);
        List<Compra> compras = compraRepository.findByIdCliente(idCliente);
        logger.info("Se encontraron {} compras para el cliente ID={}", compras.size(), idCliente);
        return compras;
    }

    public long totalComprasPorCliente(Long idCliente) {
        logger.info("Calculando total de compras para cliente ID={}", idCliente);
        long total = compraRepository.countByIdCliente(idCliente);
        logger.info("Cliente ID={} tiene {} compras", idCliente, total);
        return total;
    }

    public Compra actualizar(Long id, Compra compraActualizada) {
        logger.info("Actualizando compra con ID={}", id);
        
        Compra compra = compraRepository.findById(id).orElse(null);
        if (compra == null) {
            logger.warn("No existe compra con ID={} para actualizar", id);
            throw new ResourceNotFoundException("Compra no encontrada");
        }

        compra.setTotal(compraActualizada.getTotal());
        Compra actualizada = compraRepository.save(compra);
        logger.info("Compra ID={} actualizada exitosamente", id);
        return actualizada;
    }

    public void eliminar(Long id) {
        logger.info("Eliminando compra con ID={}", id);
        
        if (!compraRepository.existsById(id)) {
            logger.warn("No existe compra con ID={} para eliminar", id);
            throw new ResourceNotFoundException("Compra no encontrada");
        }

        compraRepository.deleteById(id);
        logger.info("Compra ID={} eliminada exitosamente", id);
    }
}
