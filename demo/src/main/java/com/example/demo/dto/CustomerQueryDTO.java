package com.example.demo.dto;

import java.util.Date;

public record CustomerQueryDTO(Integer customerId, String firstName, String lastName, String email, String filmTitle, Date rentalDate){
}
