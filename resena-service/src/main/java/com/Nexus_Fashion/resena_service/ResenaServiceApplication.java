package com.Nexus_Fashion.resena_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ResenaServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResenaServiceApplication.class, args);
	}

	// ✅ AGREGADO: Bean del WebClient con baseUrl desde properties
	@Bean
	public WebClient webClient(@Value("${api.base-url}") String baseUrl) {
		return WebClient.builder()
			.baseUrl(baseUrl) // Lee http://api-gateway:9090 del properties
			.build();
	}
}
