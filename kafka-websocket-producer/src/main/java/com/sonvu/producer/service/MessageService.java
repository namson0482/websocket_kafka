package com.sonvu.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonvu.avro.domain.SaleReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class MessageService {

    @Value("${topic.name}")
    private String topic;

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, SaleReport> userKafkaTemplate;

    @Autowired
    public MessageService(ObjectMapper objectMapper, KafkaTemplate<String, SaleReport> userKafkaTemplate) {
        this.objectMapper = objectMapper;
        this.userKafkaTemplate = userKafkaTemplate;
    }

    public String sendMessage(SaleReport saleReport) {
        Date dte=new Date();
        long milliSeconds = dte.getTime();
        ProducerRecord<String, SaleReport> producerRecord = new ProducerRecord<>(topic, milliSeconds + "", saleReport);
        userKafkaTemplate.send(producerRecord);
        return "message sent";
    }
}
