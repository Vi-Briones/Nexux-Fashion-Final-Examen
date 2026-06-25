package com.Nexus_Fashion.soporte_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "soporte")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Soporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "asunto", nullable = false, length = 150)
    private String asunto;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "prioridad", nullable = false, length = 20)
    private String prioridad;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
