package com.example.demo.repository;

import com.example.demo.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
    List<Rental> findByInventoryInventoryId(Integer inventoryId);
}
