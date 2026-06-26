package Nexus_Fashion.envio_service.dto;

import Nexus_Fashion.envio_service.model.DetalleEnvio;
import Nexus_Fashion.envio_service.model.Envio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioDTO {

    @NotNull(message = "El ID de la compra es obligatorio")
    private Long idCompra;

    @NotBlank(message = "La dirección de destino es obligatoria")
    @Size(min = 5, max = 200, message = "La dirección debe tener entre 5 y 200 caracteres")
    private String direccionDestino;

    @NotBlank(message = "La comuna es obligatoria")
    @Size(min = 2, max = 100, message = "La comuna debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "La comuna solo puede contener letras y espacios")
    private String comuna;

    public Envio toModel() {
        Envio envio = Envio.builder()
                .idCompra(this.idCompra)
                .estadoEnvio("PENDIENTE")
                .fechaEnvio(LocalDateTime.now())
                .detalles(new ArrayList<>())
                .build();

        DetalleEnvio detalle = DetalleEnvio.builder()
                .direccionDestino(this.direccionDestino)
                .comuna(this.comuna)
                .envio(envio)
                .build();

        envio.getDetalles().add(detalle);
        return envio;
    }

    public static EnvioDTO fromModel(Envio envio) {
        if (envio == null) return null;

        String direccion = "";
        String com = "";
        if (envio.getDetalles() != null && !envio.getDetalles().isEmpty()) {
            DetalleEnvio primerDetalle = envio.getDetalles().get(0);
            direccion = primerDetalle.getDireccionDestino();
            com = primerDetalle.getComuna();
        }

        return EnvioDTO.builder()
                .idCompra(envio.getIdCompra())
                .direccionDestino(direccion)
                .comuna(com)
                .build();
    }
}