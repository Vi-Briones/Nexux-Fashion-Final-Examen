package com.Nexus_Fashion.producto_service.dto;

import com.Nexus_Fashion.producto_service.model.Categoria;
import com.Nexus_Fashion.producto_service.model.Producto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Long idProducto;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long idCategoria;

    public Producto toModel() {
        Categoria cat = new Categoria();
        cat.setIdCategoria(this.idCategoria);
        return new Producto(idProducto, nombre, precio, stock, cat);
    }

    public static ProductoDTO fromModel(Producto p) {
        if (p == null) return null;
        Long catId = (p.getCategoria() != null) ? p.getCategoria().getIdCategoria() : null;
        return new ProductoDTO(p.getIdProducto(), p.getNombre(), p.getPrecio(), p.getStock(), catId);
    }
}