package Nexus_Fashion.envio_service.EnvioDTOTest;

import static org.junit.jupiter.api.Assertions.*;

import Nexus_Fashion.envio_service.dto.EnvioDTO;
import Nexus_Fashion.envio_service.model.DetalleEnvio;
import Nexus_Fashion.envio_service.model.Envio;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class EnvioDTOTest {

    @Test
    void fromModel_RetornaNull_CuandoEnvioEsNull() {
        EnvioDTO resultado = EnvioDTO.fromModel(null);
        assertNull(resultado);
    }

    @Test
    void fromModel_MapeaCorrectamenteTodosLosCampos() {
        DetalleEnvio detalle = DetalleEnvio.builder()
                .direccionDestino("Av. Providencia 1234")
                .comuna("Providencia")
                .build();

        Envio envio = Envio.builder()
                .idCompra(1L)
                .estadoEnvio("PENDIENTE")
                .fechaEnvio(LocalDateTime.now())
                .detalles(List.of(detalle))
                .build();

        EnvioDTO dto = EnvioDTO.fromModel(envio);

        assertNotNull(dto);
        assertEquals(1L, dto.getIdCompra());
        assertEquals("Av. Providencia 1234", dto.getDireccionDestino());
        assertEquals("Providencia", dto.getComuna());
    }

    @Test
    void fromModel_SinDetalles_RetornaCamposVacios() {
        Envio envio = Envio.builder()
                .idCompra(2L)
                .estadoEnvio("PENDIENTE")
                .fechaEnvio(LocalDateTime.now())
                .detalles(Collections.emptyList())
                .build();

        EnvioDTO dto = EnvioDTO.fromModel(envio);

        assertNotNull(dto);
        assertEquals(2L, dto.getIdCompra());
        assertEquals("", dto.getDireccionDestino());
        assertEquals("", dto.getComuna());
    }


    @Test
    void toModel_MapeaCorrectamenteTodosLosCampos() {
        EnvioDTO dto = new EnvioDTO(
                1L,
                "Av. Providencia 1234",
                "Providencia"
        );

        Envio envio = dto.toModel();

        assertNotNull(envio);
        assertEquals(1L, envio.getIdCompra());
        assertEquals("PENDIENTE", envio.getEstadoEnvio());
        assertNotNull(envio.getDetalles());
        assertEquals(1, envio.getDetalles().size());
        assertEquals("Av. Providencia 1234", envio.getDetalles().get(0).getDireccionDestino());
        assertEquals("Providencia", envio.getDetalles().get(0).getComuna());
    }

    @Test
    void toModel_FechaEnvioAsignadaAutomaticamente() {
        EnvioDTO dto = new EnvioDTO(
                1L,
                "Calle Los Aromos 456",
                "Las Condes"
        );

        Envio envio = dto.toModel();

        assertNotNull(envio.getFechaEnvio());
    }
}