package com.example.demo.service;

import com.example.demo.dto.CustomerUnreturnedDTO;
import com.example.demo.model.Customer;
import com.example.demo.model.Rental;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.RentalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReportService {
    private final CustomerRepository customerRepository;
    private final RentalRepository rentalRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC_NAME = "report-topic";

    public ReportService(CustomerRepository customerRepository, RentalRepository rentalRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.customerRepository = customerRepository;
        this.rentalRepository = rentalRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void generateReport() {
        LocalDate today = LocalDate.now();
        customerRepository.findNullReturnedDate()
                .stream()
                .map(c ->
                        new CustomerUnreturnedDTO(
                                c.firstName(),
                                c.lastName(),
                                c.email(),
                                c.filmTitle(),
                                (int) ChronoUnit.DAYS.between(
                                        c.rentalDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                                        today)))
                .filter(c -> c.delay() > 7)
                .map(Record::toString)
                .forEach(reportEntry -> kafkaTemplate.send(TOPIC_NAME, reportEntry));
    }

    public void deleteUnactiveClinets() {
        LocalDate today = LocalDate.now();
        List<Customer> customers = customerRepository.findActiveUsersWithNoFilmsLeftToReturn();
        for(Customer c: customers){
            Optional<Rental> optRent = rentalRepository.findLastRentByCustomerId(c.getCustomerId());
            System.out.println(c.getCustomerId());
            if(optRent.isPresent()){
                Rental rental = optRent.get();
                long diff = ChronoUnit.MONTHS.between(
                        rental.getReturnDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        today);
                if(diff > 1){
                    c.setActivebool(false);
                    customerRepository.save(c);
                    log.info("DEACTIVATED CUSTOMER WITH ID: {}",c.getCustomerId()) ;
                }
            }
        }
    }
}
