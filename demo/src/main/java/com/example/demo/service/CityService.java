package com.example.demo.service;

import com.example.demo.dto.CityDTO;
import com.example.demo.dto.CityGetDTO;
import com.example.demo.dto.CityUpdateDTO;
import com.example.demo.dto.CountryDTO;
import com.example.demo.exception.EmptyInputException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.City;
import com.example.demo.model.Country;
import com.example.demo.repository.CityRepository;
import com.example.demo.repository.CountryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
        City c = cityRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorCode.CITY_ID_NOT_FOUND.format(id)));

        return new CityGetDTO(c.getCityId(), c.getCity(),
                new CountryDTO(c.getCountry().getCountry()));
    }

    public ResponseEntity<City> create(CityDTO newCity) {
        if(newCity.city() == null || newCity.city().isEmpty())
            throw new EmptyInputException(ErrorCode.EMPTY_CITY_NAME);

        City city = new City();
        city.setCity(newCity.city());
        Country country = countryRepository.findById(newCity.countryId()).orElseThrow(() ->
                new NotFoundException(ErrorCode.COUNTRY_ID_NOT_FOUND.format(newCity.countryId())));

        city.setCountry(country);
        city.setLastUpdate(new Date());

        City saved = cityRepository.save(city);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    public ResponseEntity<City> update(Integer  id, CityUpdateDTO dto) {
        City city = cityRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorCode.CITY_ID_NOT_FOUND.format(id)));
        Country country = countryRepository.findById(dto.countryId()).orElseThrow(() ->
                new NotFoundException(ErrorCode.COUNTRY_ID_NOT_FOUND.format(dto.countryId())));

        if(dto.city() != null)
            city.setCity(dto.city());

        city.setCountry(country);

        city.setLastUpdate(new Date());
        City saved = cityRepository.save(city);
        return ResponseEntity.ok().body(saved);
    }

    public ResponseEntity<City> delete(Integer id) {
        City city = cityRepository.findById(id).orElseThrow(() ->
                new NotFoundException(ErrorCode.CITY_ID_NOT_FOUND.format(id)));
        cityRepository.delete(city);
        return ResponseEntity.ok().build();
    }
}
