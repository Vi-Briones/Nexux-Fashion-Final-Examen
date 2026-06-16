package Nexus_Fashion.envio_service.dto;

import Nexus_Fashion.envio_service.model.DetalleEnvio;
import Nexus_Fashion.envio_service.model.Envio;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioDTO {
    private Long idCompra;
    private String direccionDestino;
    private String comuna;

    // Transforma el DTO a las entidades Envio y DetalleEnvio vinculadas entre sí
    public Envio toModel() {
        // 1. Creamos la cabecera (Envio)
        Envio envio = Envio.builder()
                .idCompra(this.idCompra)
                .estadoEnvio("PENDIENTE")
                .fechaEnvio(LocalDateTime.now())
                .detalles(new ArrayList<>()) // Inicializamos la lista de detalles
                .build();

        // 2. Creamos el detalle (DetalleEnvio) y lo vinculamos con la cabecera
        DetalleEnvio detalle = DetalleEnvio.builder()
                .direccionDestino(this.direccionDestino)
                .comuna(this.comuna)
                .envio(envio) // Relación bidireccional: el detalle conoce a su envío
                .build();

        // 3. Agregamos el detalle a la lista de la cabecera
        envio.getDetalles().add(detalle);

        return envio;
    }

    // Transforma el Modelo de la base de datos de vuelta a un DTO limpio para la respuesta
    public static EnvioDTO fromModel(Envio envio) {
        if (envio == null) return null;
        
        // Tomamos el primer detalle de la lista para extraer la dirección y comuna
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
