package com.example.demo.controller;

import com.example.demo.dto.CountryDTO;
import com.example.demo.dto.CountryUpdateDTO;
import com.example.demo.model.Country;
import com.example.demo.service.CountryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/country")
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/all")
    public List<Country> getCountries(){
        log.info("GET /country/all");
        return countryService.getAll();
    }

    @GetMapping("/{id}")
    public Country getCountryById(@PathVariable Integer id){
        log.info("GET /country/{}",id);
        return countryService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Country> createCountry(@Valid @RequestBody CountryDTO country){
        log.info("POST /country RequestBody: {}", country);
        return countryService.create(country);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable Integer  id, @Valid @RequestBody CountryUpdateDTO country){
        log.info("PUT /country/{} RequestBody: {}",id, country);
        return countryService.update(id, country);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Country> deleteCountry(@PathVariable Integer  id){
        log.info("DELETE /country/{}",id);
        return countryService.delete(id);
    }
}
