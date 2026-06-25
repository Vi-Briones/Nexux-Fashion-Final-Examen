package com.Nexus_Fashion.notificacion_service.dto;

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
    private Long idUsuario;
    private String tipoEvento;
    private String mensaje;
    private Boolean leido;
    private LocalDateTime fechaEnvio;

    public Notificacion toModel() {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(this.id);
        notificacion.setIdUsuario(this.idUsuario);
        notificacion.setTipoEvento(this.tipoEvento);
        notificacion.setMensaje(this.mensaje);
        notificacion.setLeido(this.leido);
        notificacion.setFechaEnvio(this.fechaEnvio);
        return notificacion;
    }

    public static NotificacionDTO fromModel(Notificacion notificacion) {
        if (notificacion == null) {
            return null;
        }
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
