package com.example.demo.controller;

import com.example.demo.dto.CityDTO;
import com.example.demo.dto.CityGetDTO;
import com.example.demo.dto.CityUpdateDTO;
import com.example.demo.model.City;
import com.example.demo.service.CityService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/city")
public class CityController {
    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/all")
    public List<CityGetDTO> getCites(){
        log.info("GET /city/all");
        return cityService.getAll();
    }

    @GetMapping("/{id}")
    public CityGetDTO getCityById(@PathVariable Integer  id){
        log.info("GET /city/{}", id);
        return cityService.getById(id);
    }

    @PostMapping
    public ResponseEntity<City> createCity(@Valid @RequestBody CityDTO city){
        log.info("POST /city Request body: {}", city);
        return cityService.create(city);
    }

    @PutMapping("/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Integer  id, @Valid @RequestBody CityUpdateDTO city){
        log.info("PUT /city/{} Request body: {}", id, city);
        return cityService.update(id, city);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<City> deleteCustomer(@PathVariable Integer  id){
        log.info("DELETE /city/{}", id);
        return cityService.delete(id);
    }
}
