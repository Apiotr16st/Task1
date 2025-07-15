package com.example.demo.dto;

import java.time.LocalDateTime;

public record ErrorDTO (int code, String message, LocalDateTime timestamp){
}
