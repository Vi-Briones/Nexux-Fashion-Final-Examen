package com.Nexus_Fashion.resena_service.dto;

import com.Nexus_Fashion.resena_service.model.Resena;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
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
public class ResenaDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "El ID de la compra es obligatorio")
    private Long idCompra;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String cliente;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1 estrella")
    @Max(value = 5, message = "La calificación máxima es 5 estrellas")
    private Integer calificacion;

    @NotBlank(message = "El comentario es obligatorio")
    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    private String comentario;

    // Convierte este DTO a la Entidad base de la Base de Datos
    public Resena toModel() {
        return new Resena(
            id,
            idCompra,
            cliente,
            calificacion != null ? calificacion : 0,
            comentario
        );
    }

    // Convierte la Entidad de la Base de Datos a este DTO para mostrarlo fuera
    public static ResenaDTO fromModel(Resena resena) {
        return new ResenaDTO(
            resena.getId(),
            resena.getIdCompra(),
            resena.getCliente(),
            resena.getCalificacion(),
            resena.getComentario()
        );
    }
}
