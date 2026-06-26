package com.Nexus_Fashion.soporte_service;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
 
@SpringBootApplication
public class SoporteServiceeApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(SoporteServiceeApplication.class, args);
	}
 

	@Bean
	public WebClient webClient(@Value("${api.base-url}") String baseUrl) {
		return WebClient.builder()
			.baseUrl(baseUrl)
			.build();
	}
}