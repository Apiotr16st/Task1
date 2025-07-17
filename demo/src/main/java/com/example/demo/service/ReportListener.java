package com.example.demo.service;

import com.example.demo.util.SaveToFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.time.LocalDate;

@Service
@Slf4j
public class ReportListener {
    private static final String TOPIC_NAME = "report-topic";
    private static final String CONSUMER_GROUP_ID = "${spring.kafka.consumer.group-id}";

    @KafkaListener(topics = TOPIC_NAME, groupId = CONSUMER_GROUP_ID)
    public void listenReportMessages(String message) {
        try {
            SaveToFile.save("report" + LocalDate.now() + ".txt", message);
            log.info("RECIVED MESSAGE: {} SAVED TO FILE: report{}.txt", message, LocalDate.now());
        } catch (IOException e) {
            log.info("ERROR DURING GENERATING REPORT");
            throw new RuntimeException(e);
        }
    }
}