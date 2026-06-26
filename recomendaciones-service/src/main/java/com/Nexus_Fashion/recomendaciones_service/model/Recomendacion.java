package com.Nexus_Fashion.recomendaciones_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "recomendaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recomendacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long idCliente;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long idProducto;

    @NotBlank(message = "El tipo de recomendación no puede estar vacío")
    private String tipoRecomendacion;

    @NotBlank(message = "El comentario o descripción es obligatorio")
    private String comentario;

    @NotNull(message = "El puntaje de afinidad es obligatorio")
    @Min(value = 0, message = "El puntaje mínimo es 0")
    @Max(value = 100, message = "El puntaje máximo es 100")
    private Double puntajeAfinidad;

    
}
