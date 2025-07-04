package com.example.demo.controller;

import com.example.demo.dto.CountryDTO;
import com.example.demo.model.Country;
import com.example.demo.service.CountryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/country")
public class CountryController {
    @Autowired
    private CountryService countryService;

    @GetMapping("/all")
    public List<Country> getCountries(){
        return countryService.getAll();
    }

    @GetMapping("/{id}")
    public Country getCountryById(@PathVariable Integer id){
        return countryService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Country> createCountry(@Valid CountryDTO country){
        return countryService.create(country);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable Integer  id, @Valid CountryDTO country){
        return countryService.update(id, country);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Country> deleteCountry(@PathVariable Integer  id){
        return countryService.delete(id);
    }
}
