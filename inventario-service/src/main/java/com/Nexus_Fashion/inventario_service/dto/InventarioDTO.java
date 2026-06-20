package com.Nexus_Fashion.inventario_service.dto;

import com.Nexus_Fashion.inventario_service.model.Inventario;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioDTO {

    private Long id;
    private Long idProducto;
    private String sku;
    private Integer cantidadDisponible;
    private String ubicacionBodega;

    public Inventario toModel() {
        Inventario inventario = new Inventario();
        inventario.setId(this.id);
        inventario.setIdProducto(this.idProducto);
        inventario.setSku(this.sku);
        inventario.setCantidadDisponible(this.cantidadDisponible);
        inventario.setUbicacionBodega(this.ubicacionBodega);
        return inventario;
    }

    public static InventarioDTO fromModel(Inventario inventario) {
        if (inventario == null) {
            return null;
        }
        return InventarioDTO.builder()
                .id(inventario.getId())
                .idProducto(inventario.getIdProducto())
                .sku(inventario.getSku())
                .cantidadDisponible(inventario.getCantidadDisponible())
                .ubicacionBodega(inventario.getUbicacionBodega())
                .build();
    }
}
