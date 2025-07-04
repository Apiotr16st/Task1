package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressDTO(
        @NotBlank(message = "Address line 1 cannot be empty")
        @Size(max = 50, message = "Address line 1 cannot exceed 50 characters")
        String address,

        @Size(max = 50, message = "Address line 2 cannot exceed 50 characters")
        String address2,

        @NotBlank(message = "District cannot be empty")
        @Size(max = 20, message = "District cannot exceed 20 characters")
        String district,

        @NotNull(message = "City cannot be null")
        @Min(value = 0, message = "city_id cannot be negative")
        Integer city_id,

        @NotBlank(message = "Postal code cannot be empty")
        @Size(max = 10, message = "Postal code cannot exceed 10 characters")
        String postal_code,

        @NotBlank(message = "Phone number cannot be empty")
        @Size(max = 20, message = "Phone number cannot exceed 20 characters")
        String phone
){}