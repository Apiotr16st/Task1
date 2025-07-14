package com.example.demo.service;

import com.example.demo.dto.GenderDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GenderService {
    private final WebClient webClient;

    public GenderService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.genderize.io").build();
    }

    public GenderDTO getGender(String name) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("name", name).build())
                .retrieve()
                .bodyToMono(GenderDTO.class)
                .block();
    }
}
