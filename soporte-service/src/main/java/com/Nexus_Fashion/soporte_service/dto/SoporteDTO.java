package com.Nexus_Fashion.soporte_service.dto;

import java.time.LocalDateTime;

import com.Nexus_Fashion.soporte_service.model.Soporte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoporteDTO {

    private Long id;

    @NotNull(message = "El idCliente es requerido")
    private Long idCliente;

    @NotBlank(message = "El asunto es requerido")
    @Size(max = 150, message = "El asunto no puede superar los 150 caracteres")
    private String asunto;

    @NotBlank(message = "La descripción es requerida")
    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String descripcion;

    @NotBlank(message = "El estado es requerido")
    private String estado; // PENDIENTE, EN_PROCESO, RESUELTO, CERRADO

    @NotBlank(message = "La prioridad es requerida")
    private String prioridad; // BAJA, MEDIA, ALTA, CRITICA

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Soporte toModel() {
        Soporte soporte = new Soporte();
        soporte.setId(this.id);
        soporte.setIdCliente(this.idCliente);
        soporte.setAsunto(this.asunto);
        soporte.setDescripcion(this.descripcion);
        soporte.setEstado(this.estado);
        soporte.setPrioridad(this.prioridad);
        soporte.setFechaCreacion(this.fechaCreacion);
        soporte.setFechaActualizacion(this.fechaActualizacion);
        return soporte;
    }

    public static SoporteDTO fromModel(Soporte soporte) {
        if (soporte == null) return null;
        
        SoporteDTO dto = new SoporteDTO();
        dto.setId(soporte.getId());
        dto.setIdCliente(soporte.getIdCliente());
        dto.setAsunto(soporte.getAsunto());
        dto.setDescripcion(soporte.getDescripcion());
        dto.setEstado(soporte.getEstado());
        dto.setPrioridad(soporte.getPrioridad());
        dto.setFechaCreacion(soporte.getFechaCreacion());
        dto.setFechaActualizacion(soporte.getFechaActualizacion());
        return dto;
    }
}
