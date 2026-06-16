package Nexus_Fashion.envio_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Long id;
    
    @Column(name = "id_compra", nullable = false)
    private Long idCompra;
    
    @Column(name = "estado_envio", nullable = false)
    private String estadoEnvio; // "PENDIENTE", "EN_RUTA", "ENTREGADO"
    
    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    // Relación con la tabla de detalles
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "envio")
    private List<DetalleEnvio> detalles;
}
