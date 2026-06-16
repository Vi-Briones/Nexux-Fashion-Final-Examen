package Nexus_Fashion.envio_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalles_envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_envio")
    private Long id;

    @Column(name = "direccion_destino", nullable = false)
    private String direccionDestino;

    @Column(name = "comuna", nullable = false)
    private String comuna;

    // Conexión con la tabla principal de Envíos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_envio", nullable = false)
    private Envio envio;
}
