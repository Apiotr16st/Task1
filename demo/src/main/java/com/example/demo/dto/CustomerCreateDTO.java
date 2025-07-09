package com.example.demo.dto;

import com.example.demo.util.LogParser;
import jakarta.validation.constraints.*;

public record CustomerCreateDTO(

        @NotNull(message = "store_id cannot be null")
        @Min(value = 0, message = "store_id cannot be negative")
        Short storeId,

        @NotBlank(message = "first_name cannot be empty")
        @Size(max = 45, message = "first_name cannot exceed 45 characters")
        String firstName,

        @NotBlank(message = "last_name cannot be empty")
        @Size(max = 45, message = "last_name cannot exceed 45 characters")
        String lastName,

        @NotBlank(message = "email cannot be empty")
        @Size(max = 45, message = "email cannot exceed 45 characters")
        @Email(message = "Enter correct email")
        String email,

        @NotNull(message = "address cannot be null")
        @Min(value = 0, message = "address_id cannot be negative")
        Integer addressId,

        @NotNull(message = "active cannot be null")
        @Min(value = 0, message = "active must be one of {0, 1}")
        @Max(value = 1, message = "active must be one of {0, 1}")
        Integer active) {

        @Override
        public String toString() {
                return "CustomerCreateDTO[" +
                        "storeId=" + storeId + ", " +
                        "firstName='" + firstName + "', " +
                        "lastName='" + lastName + "', " +
                        "email='" + LogParser.encodeEmail(email) + "', " +
                        "addressId=" + addressId + ", " +
                        "active=" + active +
                        "]";
        }
}
