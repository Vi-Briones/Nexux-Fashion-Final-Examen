package com.Nexus_Fashion.notificacion_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Column(nullable = false)
    private Long idUsuario; 

    @NotBlank(message = "El tipo de evento no puede estar vacío")
    @Column(nullable = false)
    private String tipoEvento; 

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 500, message = "El mensaje no puede superar los 500 caracteres")
    @Column(nullable = false, length = 500)
    private String mensaje; 

    @Column(nullable = false)
    private Boolean leido;

    @Column(nullable = false)
    private LocalDateTime fechaEnvio;


    
}
