package com.Nexus_Fashion.recomendaciones_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.Nexus_Fashion.recomendaciones_service.model.Recomendacion;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecomendacionDTO {

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

    public Recomendacion toModel() {
        Recomendacion recomendacion = new Recomendacion();
        recomendacion.setId(this.id);
        recomendacion.setIdCliente(this.idCliente);
        recomendacion.setIdProducto(this.idProducto);
        recomendacion.setTipoRecomendacion(this.tipoRecomendacion);
        recomendacion.setComentario(this.comentario);
        recomendacion.setPuntajeAfinidad(this.puntajeAfinidad);
        
        return recomendacion;
    }

    public static RecomendacionDTO fromModel(Recomendacion r) {
        if (r == null) return null;

        return RecomendacionDTO.builder()
            .id(r.getId())
            .idCliente(r.getIdCliente())        
            .idProducto(r.getIdProducto())
            .tipoRecomendacion(r.getTipoRecomendacion())
            .comentario(r.getComentario())
            .puntajeAfinidad(r.getPuntajeAfinidad())
            .build();
    }
}