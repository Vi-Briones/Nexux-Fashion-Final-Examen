package com.Nexus_Fashion.compra_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)  // ✅ mapeo explícito
    private Long idCliente;

    @Column(nullable = false)
    private Double total;

@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
@JoinColumn(name = "id_compra_ref", nullable = false)  // ✅ nullable = false fuerza el orden correcto
private List<DetalleCompra> detalles;
}
