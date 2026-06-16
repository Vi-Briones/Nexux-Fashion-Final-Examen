package com.Nexus_Fashion.cliente_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;
    
    // Mapeamos la 'ñ' solo para la base de datos
    @Column(name = "contraseña", nullable = false)
    private String contrasena;

    // La relación con el Rol (Muchos usuarios tienen un rol)
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
}