package com.example.demo.repository;

import com.example.demo.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
    List<Rental> findByInventoryInventoryId(Integer inventoryId);
    @Query(value = """
    SELECT *
    FROM rental r
    WHERE r.customer_id = :customerId
    ORDER BY r.return_date DESC
    LIMIT 1
    """, nativeQuery = true)
    Optional<Rental> findLastRentByCustomerId(@Param("customerId") Integer customerId);
}
