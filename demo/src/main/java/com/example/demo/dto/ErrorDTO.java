package com.example.demo.dto;

import java.time.LocalDateTime;

public record ErrorDTO (String message, LocalDateTime timestamp){
}
