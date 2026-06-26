package com.Nexus_Fashion.inventario_service.dto;

import com.Nexus_Fashion.inventario_service.model.Inventario;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioDTO {

    private Long id;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    @NotBlank(message = "El SKU es obligatorio")
    @Size(min = 3, max = 50, message = "El SKU debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[A-Z0-9\\-]+$", message = "El SKU solo puede contener letras mayúsculas, números y guiones")
    private String sku;

    @NotNull(message = "La cantidad disponible es obligatoria")
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    private Integer cantidadDisponible;

    @NotBlank(message = "La ubicación en bodega es obligatoria")
    @Size(max = 100, message = "La ubicación no puede superar los 100 caracteres")
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
        if (inventario == null) return null;
        return InventarioDTO.builder()
                .id(inventario.getId())
                .idProducto(inventario.getIdProducto())
                .sku(inventario.getSku())
                .cantidadDisponible(inventario.getCantidadDisponible())
                .ubicacionBodega(inventario.getUbicacionBodega())
                .build();
    }
}