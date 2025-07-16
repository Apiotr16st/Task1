package com.example.demo.service;

import com.example.demo.dto.RentFilmDTO;
import com.example.demo.exception.ErrorCode;
import com.example.demo.exception.InventoryRentedException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.RentalMapper;
import com.example.demo.model.Customer;
import com.example.demo.model.Inventory;
import com.example.demo.model.Rental;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.RentalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;
    private final RentalMapper mapper;

    public RentalService(RentalRepository rentalRepository, CustomerRepository customerRepository, InventoryRepository inventoryRepository, RentalMapper mapper) {
        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.inventoryRepository = inventoryRepository;
        this.mapper = mapper;
    }

    public ResponseEntity<Object> rentFilm(RentFilmDTO rent) {
        Inventory inventory = inventoryRepository.findById(rent.inventoryId()).orElseThrow(() ->
                new NotFoundException(ErrorCode.INVENTORY_ID_NOT_FOUND.format(rent.inventoryId())));

        for (Rental r: rentalRepository.findByInventoryInventoryId(rent.inventoryId()))
            if (r.getReturnDate() == null)
                throw new InventoryRentedException(ErrorCode.INVENTORY_IS_RENTED);

        Customer customer = customerRepository.findById(rent.customerId()).orElseThrow(() ->
                new NotFoundException(ErrorCode.CUSTOMER_ID_NOT_FOUND.format(rent.customerId())));

        Rental rental = new Rental();

        rental.setInventory(inventory);
        rental.setCustomer(customer);
        rental.setStaffId(rent.staffId());

        rental.setRentalDate(new Date());
        rental.setLastUpdate(new Date());

        Rental saved = rentalRepository.save(rental);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toGetDTO(saved));
    }

    public ResponseEntity<Object> returnFilm(Integer rentalId){
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() ->
                new NotFoundException(ErrorCode.RENTAL_ID_NOT_FOUND.format(rentalId)));

        rental.setReturnDate(new Date());
        rental.setLastUpdate(new Date());

        Rental saved = rentalRepository.save(rental);
        return ResponseEntity.ok().body(mapper.toGetDTO(saved));
    }

}
