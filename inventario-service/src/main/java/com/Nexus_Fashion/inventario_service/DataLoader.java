package com.Nexus_Fashion.inventario_service;

import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.Nexus_Fashion.inventario_service.model.Inventario;
import com.Nexus_Fashion.inventario_service.repository.InventarioRepository;

import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner{

    private final InventarioRepository inventarioRepository;

    // Inyección por constructor idéntica a la del profesor
    DataLoader(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Faker faker = new Faker();
        Random random = new Random(); 

        for (int i = 0; i < 20; i++) {
            Inventario inventario = new Inventario();

            inventario.setIdProducto((long) (random.nextInt(100) + 1));
            
            String tipoRopa = faker.options().option("JEAN", "POLERA", "CHAQUETA", "POLERON");
            inventario.setSku("NEXUS-" + tipoRopa + "-" + faker.number().numberBetween(100, 999));
            
            inventario.setCantidadDisponible(faker.number().numberBetween(10, 150));
            
            String pasillo = faker.options().option("A", "B", "C");
            inventario.setUbicacionBodega("Pasillo " + pasillo + " - Estante " + faker.number().numberBetween(1, 5));

            inventarioRepository.save(inventario);
        }
        
        System.out.println(">>> ¡Datos de Inventario cargados con Faker y Random exitosamente! <<<");
    }

}
