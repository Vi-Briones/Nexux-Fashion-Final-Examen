package com.Nexus_Fashion.producto_service.service;

import org.springframework.stereotype.Service;
import com.Nexus_Fashion.producto_service.model.Producto;
import com.Nexus_Fashion.producto_service.repository.ProductoRepository;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto guardar(Producto producto) {
        logger.info("Servicio Producto: Intentando registrar el producto [{}] en el catálogo", producto.getNombre());
        
        // Mantenemos la validación de negocio original de tu compañero
        if (producto.getIdProducto() == null && productoRepository.existsByNombre(producto.getNombre())) {
            logger.warn("Servicio Producto - ADVERTENCIA: Intento de registro duplicado. El producto con nombre [{}] ya existe en el inventario.", producto.getNombre());
            throw new RuntimeException("El producto ya existe en el inventario.");
        }
        
        try {
            Producto productoGuardado = productoRepository.save(producto);
            logger.info("Servicio Producto: Producto almacenado con éxito con el ID={}", productoGuardado.getIdProducto());
            return productoGuardado;
        } catch (Exception e) {
            logger.error("Servicio Producto - ERROR CRÍTICO: Falló la inserción del producto en la base de datos", e);
            throw e;
        }
    }

    public List<Producto> listar() {
        logger.info("Servicio Producto: Consultando todos los productos del inventario en la BD");
        List<Producto> lista = productoRepository.findAll();
        logger.info("Servicio Producto: Consulta finalizada. Total de productos en catálogo: {}", lista.size());
        return lista;
    }

    public boolean existePorId(Long id) {
        logger.info("Servicio Producto: Evaluando existencia en el inventario para el ID={}", id);
        
        boolean existe = productoRepository.existsById(id);
        
        if (existe) {
            logger.info("Servicio Producto: Verificación exitosa. El ID={} SÍ existe en catálogo.", id);
        } else {
            logger.warn("Servicio Producto - ADVERTENCIA: El ID={} NO corresponde a ningún producto del inventario.", id);
        }
        
        return existe;
    }

    public Producto buscarPorId(Long id) {
        logger.info("Servicio Producto: Buscando producto con ID={}", id);
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            logger.warn("Servicio Producto - ADVERTENCIA: No se encontró producto con ID={}", id);
        } else {
            logger.info("Servicio Producto: Producto encontrado con ID={}", id);
        }
        return producto;
    }

    public Producto actualizar(Long id, Producto productoActualizado) {
        logger.info("Servicio Producto: Iniciando actualización del producto con ID={}", id);
        
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            logger.warn("Servicio Producto - ERROR: No existe producto con ID={} para actualizar", id);
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }

        producto.setNombre(productoActualizado.getNombre());
        producto.setPrecio(productoActualizado.getPrecio());
        producto.setStock(productoActualizado.getStock());
        if (productoActualizado.getCategoria() != null) {
            producto.setCategoria(productoActualizado.getCategoria());
        }

        Producto actualizado = productoRepository.save(producto);
        logger.info("Servicio Producto: Producto ID={} actualizado exitosamente", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("Servicio Producto: Iniciando eliminación del producto con ID={}", id);
        
        if (!productoRepository.existsById(id)) {
            logger.warn("Servicio Producto - ERROR: No existe producto con ID={} para eliminar", id);
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }

        productoRepository.deleteById(id);
        logger.info("Servicio Producto: Producto ID={} eliminado exitosamente", id);
    }
}