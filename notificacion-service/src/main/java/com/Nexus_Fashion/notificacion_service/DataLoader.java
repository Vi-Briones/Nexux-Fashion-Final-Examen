package com.Nexus_Fashion.notificacion_service;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.Nexus_Fashion.notificacion_service.model.Notificacion;
import com.Nexus_Fashion.notificacion_service.repository.NotificacionRepository;

import net.datafaker.Faker;

@Component
public class DataLoader implements CommandLineRunner{

    private final NotificacionRepository notificacionRepository;

    DataLoader(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random();

  
        for (int i = 0; i < 5; i++) {
            Notificacion notificacion = new Notificacion();

            notificacion.setIdUsuario((long) random.nextInt(5) + 1);
            notificacion.setTipoEvento(faker.options().option(
                "COMPRA_CONFIRMADA", "DESPACHO_EN_CAMINO", "STOCK_BAJO", "RESENA_NUEVA"
            ));
            notificacion.setMensaje("Alerta de Nexus Fashion: " + faker.commerce().productName() + " presenta novedades en el sistema.");
            notificacion.setLeido(random.nextBoolean());
            notificacion.setFechaEnvio(LocalDateTime.now());
            notificacionRepository.save(notificacion);
        }
    }
}
