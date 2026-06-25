package com.Nexus_Fashion.resena_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Conexión con el microservicio de Compras
    @NotNull(message = "El ID de la compra no puede ser nulo")
    private Long idCompra;

    @NotBlank(message = "El nombre del cliente no puede estar vacío")
    private String cliente;

    // Validación para que la calificación sea entre 1 y 5 estrellas
    @Min(value = 1, message = "La calificación mínima es 1 estrella")
    @Max(value = 5, message = "La calificación máxima es 5 estrellas")
    private int calificacion;

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 500, message = "El comentario no puede pasar de los 500 caracteres")
    private String comentario;
}
