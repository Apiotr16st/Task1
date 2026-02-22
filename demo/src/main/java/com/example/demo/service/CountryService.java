package com.example.demo.service;

import com.example.demo.dto.CountryDTO;
import com.example.demo.dto.CountryUpdateDTO;
import com.example.demo.exception.ErrorCode;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Country;
import com.example.demo.repository.CountryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CountryService {
    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<Country> getAll() {
        return countryRepository.findAll();
    }

    public Country getById(Integer  id) {
        return countryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorCode.CITY_ID_NOT_FOUND.format(id)));
    }

    public ResponseEntity<Country> create(CountryDTO newCountry) {
        Country country = new Country();
        country.setCountry(newCountry.country());
        country.setLastUpdate(new Date());
        Country saved = countryRepository.save(country);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    public ResponseEntity<Country> update(Integer  id, CountryUpdateDTO dto) {
        Country country = countryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorCode.COUNTRY_ID_NOT_FOUND.format(id)));
        country.setCountry(dto.country());
        Country saved = countryRepository.save(country);
        return ResponseEntity.ok().body(saved);
    }

    public ResponseEntity<Country> delete(Integer  id) {
        Country country = countryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorCode.COUNTRY_ID_NOT_FOUND));
        countryRepository.delete(country);
        return ResponseEntity.ok().build();
    }
}
