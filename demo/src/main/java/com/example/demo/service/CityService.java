package com.example.demo.service;

import com.example.demo.dto.CityDTO;
import com.example.demo.dto.CityGetDTO;
import com.example.demo.dto.CityUpdateDTO;
import com.example.demo.dto.CountryDTO;
import com.example.demo.model.City;
import com.example.demo.model.Country;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.CountryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public CityService(CityRepository cityRepository, CountryRepository countryRepository) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    public List<CityGetDTO> getAll() {
        List<City> cities = cityRepository.findAll();
        return cities.stream().map(
                c -> new CityGetDTO(c.getCityId(), c.getCity(),
                        new CountryDTO(c.getCountry().getCountry()))).toList();
    }

    public CityGetDTO getById(Integer id) {
        City c = cityRepository.findById(id).orElseThrow(() ->  new EntityNotFoundException("cityId"));

        return new CityGetDTO(c.getCityId(), c.getCity(),
                new CountryDTO(c.getCountry().getCountry()));
    }

    public ResponseEntity<City> create(CityDTO newCity) {
        if(newCity.countryId() == null || newCity.city() == null || newCity.city().isEmpty()){
            throw new DataIntegrityViolationException("City name and country cannot be empty");
        }

        City city = new City();
        city.setCity(newCity.city());
        Country country = countryRepository.findById(newCity.countryId()).orElseThrow(() -> new EntityNotFoundException("countryId"));
        city.setCountry(country);
        city.setLastUpdate(new Date());

        City saved = cityRepository.save(city);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    public ResponseEntity<City> update(Integer  id, CityUpdateDTO dto) {

        City city = cityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("cityId"));
        Country country = countryRepository.findById(dto.countryId()).orElseThrow(() -> new EntityNotFoundException("countryId"));

        if(dto.city() != null)
            city.setCity(dto.city());

        city.setCountry(country);

        city.setLastUpdate(new Date());
        City saved = cityRepository.save(city);
        return ResponseEntity.ok().body(saved);
    }

    public ResponseEntity<City> delete(Integer id) {
        Optional<City> city = cityRepository.findById(id);
        if(city.isPresent()){
            cityRepository.delete(city.get());
            return ResponseEntity.ok().build();
        }
        else{
            throw new EntityNotFoundException("cityId");
        }
    }
}
