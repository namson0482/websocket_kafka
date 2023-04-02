package com.sonvu.producer.service;

import com.sonvu.avro.domain.SaleReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class MessageService {

    private final KafkaTemplate<String, SaleReport> kafkaTemplate;
    private final KafkaProperties kafkaProperties;

    public MessageService(KafkaTemplate<String, SaleReport> kafkaTemplate, KafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }


    public String sendMessage(SaleReport saleReport) {
        Date dte=new Date();
        long milliSeconds = dte.getTime();

        String kafkaTopic = kafkaProperties.getProducer().getProperties().get("topic");
        log.info("Sending News '{}' to topic '{}'", saleReport.getStartDate(), kafkaTopic);
        kafkaTemplate.send(kafkaTopic, milliSeconds + "", saleReport);
        return "message sent";
    }
}
