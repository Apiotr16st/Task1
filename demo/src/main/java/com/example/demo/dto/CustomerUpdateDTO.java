package com.example.demo.dto;

import com.example.demo.util.LogParser;
import jakarta.validation.constraints.*;

public record CustomerUpdateDTO(
                                 Short storeId,

                                 @Size(max = 45, message = "first_name cannot exceed 45 characters")
                                 String firstName,

                                 @Size(max = 45, message = "last_name cannot exceed 45 characters")
                                 String lastName,

                                 @Size(max = 45, message = "email cannot exceed 45 characters")
                                 @Email
                                 String email,

                                 Integer addressId,

                                 Integer active,

                                Boolean activebool) {

    @Override
    public String toString() {
        return "CustomerUpdateDTO[" +
                "storeId=" + storeId + ", " +
                "firstName='" + firstName + "', " +
                "lastName='" + lastName + "', " +
                "email='" + LogParser.encodeEmail(email) + "', " +
                "addressId=" + addressId + ", " +
                "active=" + active + "," +
                "activebool" + activebool +
                "]";
    }
}