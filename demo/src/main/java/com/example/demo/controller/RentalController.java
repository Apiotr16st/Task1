package com.example.demo.controller;

import com.example.demo.dto.RentFilmDTO;
import com.example.demo.service.RentalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/rental")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService){
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<Object> rentFilm(@RequestBody RentFilmDTO rent){
        log.info("POST /rental");
        return rentalService.rentFilm(rent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> returnFilm(@PathVariable Integer id){
        log.info("PUT /rental/{}",id);
        return rentalService.returnFilm(id);
    }
}
