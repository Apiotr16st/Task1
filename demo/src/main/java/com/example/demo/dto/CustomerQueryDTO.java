package com.example.demo.dto;

import java.util.Date;

public record CustomerQueryDTO(String firstName, String lastName, String email, String filmTitle, Date rentalDate){
}
