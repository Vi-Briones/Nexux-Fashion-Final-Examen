package com.Nexus_Fashion.compra_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.Nexus_Fashion.compra_service.model.Compra;
import com.Nexus_Fashion.compra_service.model.DetalleCompra;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {

    private Long id;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    private Double precioUnitario;

    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a 0")
    private Double total;
    
    public Compra toModel() {
        DetalleCompra detalle = new DetalleCompra();
        detalle.setIdProducto(this.idProducto);
        detalle.setCantidad(this.cantidad);
        detalle.setPrecioUnitario(this.precioUnitario);

        List<DetalleCompra> detalles = new ArrayList<>();
        detalles.add(detalle);

        Compra compra = new Compra();
        compra.setId(this.id);
        compra.setIdCliente(this.idCliente);
        compra.setTotal(this.total);
        compra.setDetalles(detalles);

        return compra;
    }

    public static CompraDTO fromModel(Compra c) {
        if (c == null) return null;

        DetalleCompra d = (c.getDetalles() != null && !c.getDetalles().isEmpty())
                          ? c.getDetalles().get(0) : new DetalleCompra();

        return new CompraDTO(
            c.getId(),
            c.getIdCliente(),
            d.getIdProducto(),
            d.getCantidad(),
            d.getPrecioUnitario(),
            c.getTotal()
        );
    }
}