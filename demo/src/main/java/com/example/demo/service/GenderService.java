package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GenderService {
    private final WebClient webClient;

    public GenderService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.genderize.io").build();
    }

    private record Gender(String name, String gender, double probability, int count) {}

    public String getGender(String name) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("name", name).build())
                .retrieve()
                .bodyToMono(Gender.class)
                .map(Gender::gender)
                .block();
    }
}
