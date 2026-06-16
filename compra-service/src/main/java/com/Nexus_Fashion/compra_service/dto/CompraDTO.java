package com.Nexus_Fashion.compra_service.dto;

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
    private Long idCliente;
    private Long idProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double total;

    // MÉTODO TO_MODEL: Convierte el DTO plano en la estructura de 2 tablas
    public Compra toModel() {
        // 1. Creamos el objeto para la tabla de detalle
        DetalleCompra detalle = new DetalleCompra();
        detalle.setIdProducto(this.idProducto);
        detalle.setCantidad(this.cantidad);
        detalle.setPrecioUnitario(this.precioUnitario);

        // 2. Metemos el detalle en una lista (requisito del modelo)
        List<DetalleCompra> detalles = new ArrayList<>();
        detalles.add(detalle);

        // 3. Retornamos la Compra con su lista de detalles
        Compra compra = new Compra();
        compra.setId(this.id);
        compra.setIdCliente(this.idCliente);
        compra.setTotal(this.total);
        compra.setDetalles(detalles);

        return compra;
    }

    // MÉTODO FROM_MODEL: Convierte las 2 tablas en un DTO plano para mostrarlo
    public static CompraDTO fromModel(Compra c) {
        if (c == null) return null;

        // Extraemos el primer detalle de la lista para aplanar los datos
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
