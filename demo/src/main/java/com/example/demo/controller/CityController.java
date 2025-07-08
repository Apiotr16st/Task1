package com.example.demo.controller;

import com.example.demo.dto.CityDTO;
import com.example.demo.dto.CityGetDTO;
import com.example.demo.dto.CityUpdateDTO;
import com.example.demo.model.City;
import com.example.demo.service.CityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/all")
    public List<CityGetDTO> getCites(){
        return cityService.getAll();
    }

    @GetMapping("/{id}")
    public CityGetDTO getCityById(@PathVariable Integer  id){
        return cityService.getById(id);
    }

    @PostMapping
    public ResponseEntity<City> createCity(@Valid @RequestBody CityDTO city){
        return cityService.create(city);
    }

    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Integer  id, @Valid @RequestBody CityUpdateDTO city){
        return cityService.update(id, city);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<City> deleteCustomer(@PathVariable Integer  id){
        return cityService.delete(id);
    }
}
