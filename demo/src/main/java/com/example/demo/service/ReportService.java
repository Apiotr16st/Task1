package com.example.demo.service;

import com.example.demo.dto.CustomerQueryDTO;
import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReportService {
    private final CustomerRepository customerRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC_NAME = "report-topic";

    public ReportService(CustomerRepository customerRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.customerRepository = customerRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    private int dayDiff(Date date){
        return (int) ChronoUnit.DAYS.between(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDateTime.now());
    }

    private String toReportLine(CustomerQueryDTO dto, List<UnreturnedFilm> list) {
        return String.format(
                "firstName=%s, lastName=%s, email=%s, films=%s",
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                list
        );
    }

    private record UnreturnedFilm(String filmName, Integer delay){};

    public void generateReport() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        List<CustomerQueryDTO> query = customerRepository.findNullReturnedDate(weekAgo);
        Map<Integer, List<UnreturnedFilm>> integerkrotkaMap = query
                .stream()
                .collect(Collectors.groupingBy(
                        CustomerQueryDTO::customerId,
                        Collectors.mapping(
                                dto -> new UnreturnedFilm(dto.filmTitle(), dayDiff(dto.rentalDate())),
                                Collectors.toList()
                        )
                ));

        query.stream()
                .map(c-> toReportLine(c, integerkrotkaMap.get(c.customerId())))
                .distinct()
                .forEach(reportEntry -> kafkaTemplate.send(TOPIC_NAME, reportEntry));
    }

    public void deleteUnactiveClinets() {
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(2);
        List<CustomerQueryDTO> customers = customerRepository.findActiveUsersWithNoFilmsLeftToReturn(monthAgo);
        for(CustomerQueryDTO dto: customers){
            Customer c = customerRepository.findById(dto.customerId()).get();
            c.setActivebool(false);
            customerRepository.save(c);
            log.info("DEACTIVATED CUSTOMER WITH ID: {}",c.getCustomerId()) ;
        }
    }
}
