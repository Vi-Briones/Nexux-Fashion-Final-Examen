package com.Nexus_Fashion.resena_service;

import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.Nexus_Fashion.resena_service.model.Resena;
import com.Nexus_Fashion.resena_service.repository.ResenaRepository;

import net.datafaker.Faker;

@Component
public class DataLoader implements CommandLineRunner{

    private final ResenaRepository resenaRepository;

    // Inyección limpia por constructor
    public DataLoader(ResenaRepository resenaRepository) {
        this.resenaRepository = resenaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker(); //
        Random random = new Random(); //

        for (int i = 0; i < 5; i++) { //
            Resena resena = new Resena(); //

            // Vincula la reseña a un ID de compra aleatorio entre 1 y 3
            resena.setIdCompra((long) random.nextInt(10) + 1);

            // Genera datos usando Faker y variables locales
            String clienteAleatorio = faker.name().fullName();
            int calificacionAleatoria = random.nextInt(5) + 1; // De 1 a 5 estrellas
            
            String comentarioAleatorio = faker.options().option(
                "Excelente calidad de tela, el polerón me quedó perfecto.",
                "El color de la polera es un poco diferente al de la foto, pero está bonito.",
                "Muy cómodo, abrigador y la talla del jean corresponde totalmente.",
                "Llegó súper rápido el envío, lo recomiendo a ojos cerrados.",
                "La chaqueta es linda pero se demoró un par de días más en llegar."
            );

            resena.setCliente(clienteAleatorio);
            resena.setCalificacion(calificacionAleatoria);
            resena.setComentario(comentarioAleatorio);

            resenaRepository.save(resena);
        }
    }
}
