package com.Nexus_Fashion.venta_service.dto;

import com.Nexus_Fashion.venta_service.model.DetalleVenta;
import com.Nexus_Fashion.venta_service.model.MetodoPago;
import com.Nexus_Fashion.venta_service.model.Venta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {

    private Long idVenta;
    private Long idCliente;
    private Date fecha;
    private Double total;
    
    // Solo pedimos el ID del Metodo de Pago en Postman (Igual que idRol)
    private Long idMetodoPago; 

    // Usamos una clase interna estática para cumplir la regla de "1 solo archivo DTO"
    private List<DetalleItem> detalles;

    // --- SUB-CLASE INTERNA PARA LOS DETALLES ---
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleItem {
        private Long idProducto;
        private Integer cantidad;
    }
    // -------------------------------------------

    public Venta toModel() {
        // 1. Creamos el MetodoPago "falso" solo con el ID para que JPA haga la relación mágica
        MetodoPago mp = new MetodoPago();
        mp.setIdMetodoPago(this.idMetodoPago);

        // 2. Creamos la venta principal (detalles nulos por ahora)
        Venta venta = new Venta(this.idVenta, this.idCliente, new Date(), this.total, mp, null);

        // 3. Mapeamos la lista de detalles y le asignamos esta venta a cada uno
        if (this.detalles != null) {
            List<DetalleVenta> listaDetalles = this.detalles.stream()
                .map(d -> new DetalleVenta(null, venta, d.getIdProducto(), d.getCantidad()))
                .collect(Collectors.toList());
            
            venta.setDetalles(listaDetalles);
        }

        return venta;
    }

    public static VentaDTO fromModel(Venta v) {
        if (v == null) return null;
        
        // Extraemos el ID del MetodoPago (Igual que extrajimos el Rol)
        Long metodoPagoId = (v.getMetodoPago() != null) ? v.getMetodoPago().getIdMetodoPago() : null;

        // Extraemos todos los detalles (Sin perder información)
        List<DetalleItem> listaDetalles = null;
        if (v.getDetalles() != null) {
            listaDetalles = v.getDetalles().stream()
                .map(d -> new DetalleItem(d.getIdProducto(), d.getCantidad()))
                .collect(Collectors.toList());
        }

        // Devolvemos el DTO usando el constructor completo
        return new VentaDTO(
                v.getIdVenta(), 
                v.getIdCliente(), 
                v.getFecha(), 
                v.getTotal(), 
                metodoPagoId, 
                listaDetalles
        );
    }
}