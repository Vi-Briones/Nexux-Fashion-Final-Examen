package com.Nexus_Fashion.compra_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.Nexus_Fashion.compra_service.exception.BadRequestException;
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

    public CompraService(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
        this.webClient = WebClient.create(); 
    }

    public Compra guardar(Compra compra) {
        logger.info("Iniciando proceso de guardado de compra: idCliente={}, idProducto={}",
                compra.getIdCliente(), compra.getDetalles().get(0).getIdProducto());
        Boolean existeCliente;
        Boolean existeProducto;
        try {
            logger.debug("Validando existencia de cliente id={}", compra.getIdCliente());
            existeCliente = webClient.get()
                    .uri(String.format(clientePath, compra.getIdCliente()))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            logger.debug("Respuesta existencia cliente: {}", existeCliente);
        } catch (Exception e) {
            logger.error("Error al validar cliente id={}", compra.getIdCliente(), e);
            throw new BadRequestException("Error al validar cliente");
        }
        try {
            Long idProducto = compra.getDetalles().get(0).getIdProducto();
            logger.debug("Validando existencia de producto id={}", idProducto);
            existeProducto = webClient.get()
                    .uri(String.format(productoPath, idProducto))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            logger.debug("Respuesta existencia producto: {}", existeProducto);
        } catch (Exception e) {
            logger.error("Error al validar producto id={}", compra.getDetalles().get(0).getIdProducto(), e);
            throw new BadRequestException("Error al validar producto");
        }
        // Validaciones de negocio
        if (existeCliente == null) {
            logger.warn("Respuesta nula al validar cliente id={}", compra.getIdCliente());
            throw new BadRequestException("No se pudo validar la existencia del cliente");
        }
        if (Boolean.FALSE.equals(existeCliente)) {
            logger.warn("Cliente no existe id={}", compra.getIdCliente());
            throw new ResourceNotFoundException("Cliente no existe");
        }
        if (existeProducto == null) {
            logger.warn("Respuesta nula al validar producto id={}", compra.getDetalles().get(0).getIdProducto());
            throw new BadRequestException("No se pudo validar la existencia del producto");
        }
        if (Boolean.FALSE.equals(existeProducto)) {
            logger.warn("Producto no existe id={}", compra.getDetalles().get(0).getIdProducto());
            throw new ResourceNotFoundException("Producto no existe");
        }
        Compra compraGuardada = compraRepository.save(compra);
        logger.info("Compra guardada exitosamente con id={}", compraGuardada.getId());
        return compraGuardada;
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
