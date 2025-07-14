package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmailService {
    private final WebClient webClient;

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://www.disify.com")
                .build();
    }

    private record Email(Boolean format, String domain, Boolean disposable, Boolean dns){}

    public Boolean validateEmail(String email) {
        return webClient.get()
                .uri("/api/email/" + email)
                .retrieve()
                .bodyToMono(Email.class)
                .map(Email::disposable)
                .block();
    }
}

