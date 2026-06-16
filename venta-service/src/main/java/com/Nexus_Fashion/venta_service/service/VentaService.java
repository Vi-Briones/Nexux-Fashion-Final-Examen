package com.Nexus_Fashion.venta_service.service;

import com.Nexus_Fashion.venta_service.dto.VentaDTO;
import com.Nexus_Fashion.venta_service.model.Venta;
import com.Nexus_Fashion.venta_service.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentaService {

    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Transactional
    public VentaDTO guardar(VentaDTO dto) {
        logger.info("Servicio Ventas: Iniciando el procesamiento de una nueva orden de venta");

        // Validación 1: Verificar que la venta tenga detalles asociados
        if (dto.getIdVenta() == null && (dto.getDetalles() == null || dto.getDetalles().isEmpty())) {
            logger.warn("Servicio Ventas - RECHAZADO: Intento de registrar una venta sin ítems o detalles de producto.");
            throw new RuntimeException("Error: Una venta debe contener al menos un detalle de producto.");
        }

        // Validación 2: Verificar que el total sea un valor válido
        if (dto.getTotal() == null || dto.getTotal() <= 0) {
            logger.warn("Servicio Ventas - RECHAZADO: Intento de registrar venta con total inválido o en cero ({})", dto.getTotal());
            throw new RuntimeException("Error: El total de la venta debe ser mayor a 0.");
        }

        try {
            logger.info("Servicio Ventas: Mapeando DTO a modelo y procediendo con la persistencia en BD");
            Venta venta = dto.toModel();
            Venta ventaGuardada = ventaRepository.save(venta);
            
            logger.info("Servicio Ventas: Transacción guardada con éxito en la base de datos con ID={}", ventaGuardada.getIdVenta());
            return VentaDTO.fromModel(ventaGuardada);
            
        } catch (Exception e) {
            logger.error("Servicio Ventas - ERROR CRÍTICO: No se pudo completar la transacción en la BD", e);
            throw e;
        }
    }

    public List<VentaDTO> listar() {
        logger.info("Servicio Ventas: Extrayendo el historial completo de ventas desde la BD");
        
        List<VentaDTO> lista = ventaRepository.findAll().stream()
                .map(VentaDTO::fromModel)
                .collect(Collectors.toList());
                
        logger.info("Servicio Ventas: Historial recuperado. Total de transacciones: {}", lista.size());
        return lista;
    }

    public VentaDTO buscarPorId(Long id) {
        logger.info("Servicio Ventas: Buscando comprobante de venta para el ID={}", id);
        
        Venta venta = ventaRepository.findById(id).orElse(null);
        if (venta == null) {
            logger.warn("Servicio Ventas - ADVERTENCIA: No se encontró ningún registro de venta con el ID={}", id);
            return null;
        }
        
        logger.info("Servicio Ventas: Registro de venta ID={} recuperado con éxito", id);
        return VentaDTO.fromModel(venta);
    }

    public boolean existePorId(Long id) {
        logger.info("Servicio Ventas: Validando si existe la transacción ID={} en el sistema", id);
        
        boolean existe = ventaRepository.existsById(id);
        if (existe) {
            logger.info("Servicio Ventas: La transacción ID={} está confirmada en BD", id);
        } else {
            logger.warn("Servicio Ventas - ADVERTENCIA: El ID de transacción={} es inexistente", id);
        }
        return existe;
    }

    public List<VentaDTO> listarPorCliente(Long idCliente) {
        logger.info("Servicio Ventas: Buscando historial de compras asociadas al Cliente ID={}", idCliente);
        
        List<VentaDTO> listaPorCliente = ventaRepository.findByIdCliente(idCliente).stream()
                .map(VentaDTO::fromModel)
                .collect(Collectors.toList());
                
        logger.info("Servicio Ventas: Búsqueda finalizada. Se encontraron {} ventas para el Cliente ID={}", listaPorCliente.size(), idCliente);
        return listaPorCliente;
    }

    public long totalVentasPorCliente(Long idCliente) {
        logger.info("Servicio Ventas: Contando total de ventas para Cliente ID={}", idCliente);
        long total = ventaRepository.countByIdCliente(idCliente);
        logger.info("Servicio Ventas: Cliente ID={} tiene {} ventas", idCliente, total);
        return total;
    }

    public VentaDTO actualizar(Long id, VentaDTO ventaActualizada) {
        logger.info("Servicio Ventas: Actualizando venta con ID={}", id);
        
        Venta venta = ventaRepository.findById(id).orElse(null);
        if (venta == null) {
            logger.warn("Servicio Ventas - ERROR: No existe venta con ID={}", id);
            throw new RuntimeException("Venta no encontrada");
        }

        venta.setTotal(ventaActualizada.getTotal());
        Venta actualizada = ventaRepository.save(venta);
        logger.info("Servicio Ventas: Venta ID={} actualizada exitosamente", id);
        return VentaDTO.fromModel(actualizada);
    }

    public void eliminar(Long id) {
        logger.info("Servicio Ventas: Eliminando venta con ID={}", id);
        
        if (!ventaRepository.existsById(id)) {
            logger.warn("Servicio Ventas - ERROR: No existe venta con ID={}", id);
            throw new RuntimeException("Venta no encontrada");
        }

        ventaRepository.deleteById(id);
        logger.info("Servicio Ventas: Venta ID={} eliminada exitosamente", id);
    }
}


