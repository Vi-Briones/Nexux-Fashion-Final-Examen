package com.Nexus_Fashion.notificacion_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.Nexus_Fashion.notificacion_service.model.Notificacion;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionDTO {

    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;

    @NotBlank(message = "El tipo de evento es obligatorio")
    @Size(max = 100, message = "El tipo de evento no puede superar los 100 caracteres")
    private String tipoEvento;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 5, max = 500, message = "El mensaje debe tener entre 5 y 500 caracteres")
    private String mensaje;

    @NotNull(message = "El campo leído es obligatorio")
    private Boolean leido;

    private LocalDateTime fechaEnvio;

    public Notificacion toModel() {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(this.id);
        notificacion.setIdUsuario(this.idUsuario);
        notificacion.setTipoEvento(this.tipoEvento);
        notificacion.setMensaje(this.mensaje);
        notificacion.setLeido(this.leido);
        notificacion.setFechaEnvio(this.fechaEnvio != null ? this.fechaEnvio : LocalDateTime.now());
        return notificacion;
    }

    public static NotificacionDTO fromModel(Notificacion notificacion) {
        if (notificacion == null) return null;
        return NotificacionDTO.builder()
                .id(notificacion.getId())
                .idUsuario(notificacion.getIdUsuario())
                .tipoEvento(notificacion.getTipoEvento())
                .mensaje(notificacion.getMensaje())
                .leido(notificacion.getLeido())
                .fechaEnvio(notificacion.getFechaEnvio())
                .build();
    }
}