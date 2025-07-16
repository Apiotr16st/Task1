package com.example.demo.scheduled;

import com.example.demo.service.ReportService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class Scheduler {
    private final ReportService reportService;

    public Scheduler(ReportService reportService) {
        this.reportService = reportService;
    }

    @Scheduled(cron = "0 0 7 ? * MON", zone="Europe/Warsaw")
    public void customersWithUnreturnedFilms(){
        reportService.generateReport();
    }

    @Scheduled(cron = "0 0 23 ? * *", zone="Europe/Warsaw")
    public void checkActiveClient(){
        reportService.deleteUnactiveClinets();
    }
}
