package com.Nexus_Fashion.producto_service.dto;

import com.Nexus_Fashion.producto_service.model.Categoria;
import com.Nexus_Fashion.producto_service.model.Producto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Long idProducto;
    private String nombre;
    private Double precio;
    private Integer stock;
    
    // Solo pedimos el ID de la categoría
    private Long idCategoria; 

    public Producto toModel() {
        // Creamos la categoría "ficticia" solo con el ID
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