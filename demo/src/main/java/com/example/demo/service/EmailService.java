package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmailService {
    private final WebClient webClient;

    public EmailService(WebClient.Builder webClientBuilder, @Value("${email.validation.api}") String apiUrl) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .build();
    }

    private record Email(Boolean format, String domain, Boolean disposable, Boolean dns){}

    public Boolean validateEmail(String email) {
        return webClient.get()
                .uri(email)
                .retrieve()
                .bodyToMono(Email.class)
                .map(Email::disposable)
                .block();
    }
}

