package com.Nexus_Fashion.inventario_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Long id;

    // Relación lógica con el ID del producto que viene del microservicio de productos
    @Column(name = "id_producto", nullable = false)
    private Long idProducto;

    // Código único del producto (Stock Keeping Unit) para control de tienda (ej: NX-POL-OVR-01)
    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;

    @Column(name = "ubicacion_bodega", nullable = false)
    private String ubicacionBodega;
}
