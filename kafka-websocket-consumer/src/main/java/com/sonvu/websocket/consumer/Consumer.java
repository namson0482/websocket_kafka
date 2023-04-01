package com.sonvu.websocket.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.sonvu.avro.domain.SaleReport;
import com.sonvu.websocket.config.ApplicationBean;
import com.sonvu.websocket.controller.WebSocketController;
import com.sonvu.websocket.service.MessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Component
public class Consumer {

    private static final String orderTopic = "${topic.name}";

    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final MessageService messageService;

    static final String TOTAL_ITEM_RECEIVED = "Just consume a message and total items";
    private final WebSocketController webSocketController;

    @Autowired
    ApplicationBean applicationBean;

    @Autowired
    public Consumer(ObjectMapper objectMapper, ModelMapper modelMapper, MessageService messageService, WebSocketController webSocketController) {
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.messageService = messageService;
        this.webSocketController = webSocketController;
    }

    @KafkaListener(topics = orderTopic)
    public void consumeMessage(SaleReport saleReport) {
        try {
            messageService.persistMessage(saleReport);
            log.info(TOTAL_ITEM_RECEIVED);
        } catch (CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        } catch (CsvDataTypeMismatchException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

@Data
class Product {
    String name;
    int quantity;
    BigDecimal money;
}
