package com.Nexus_Fashion.venta_service.dto;

import com.Nexus_Fashion.venta_service.model.DetalleVenta;
import com.Nexus_Fashion.venta_service.model.MetodoPago;
import com.Nexus_Fashion.venta_service.model.Venta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;

    private Date fecha;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    private Double total;

    @NotNull(message = "El ID del método de pago es obligatorio")
    private Long idMetodoPago;

    @NotEmpty(message = "Debe incluir al menos un detalle de venta")
    @Valid
    private List<DetalleItem> detalles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleItem {

        @NotNull(message = "El ID del producto en el detalle es obligatorio")
        private Long idProducto;

        @NotNull(message = "La cantidad en el detalle es obligatoria")
        @Min(value = 1, message = "La cantidad mínima por producto es 1")
        private Integer cantidad;
    }

    public Venta toModel() {
        MetodoPago mp = new MetodoPago();
        mp.setIdMetodoPago(this.idMetodoPago);

        Venta venta = new Venta(this.idVenta, this.idCliente, new Date(), this.total, mp, null);

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

        Long metodoPagoId = (v.getMetodoPago() != null) ? v.getMetodoPago().getIdMetodoPago() : null;

        List<DetalleItem> listaDetalles = null;
        if (v.getDetalles() != null) {
            listaDetalles = v.getDetalles().stream()
                .map(d -> new DetalleItem(d.getIdProducto(), d.getCantidad()))
                .collect(Collectors.toList());
        }

        return new VentaDTO(v.getIdVenta(), v.getIdCliente(), v.getFecha(), v.getTotal(), metodoPagoId, listaDetalles);
    }
}