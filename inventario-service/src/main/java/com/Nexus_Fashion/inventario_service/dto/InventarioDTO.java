package com.Nexus_Fashion.inventario_service.dto;

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
}
