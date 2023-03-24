package com.hasee.websocket.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasee.websocket.controller.WebSocketController;
import com.hasee.websocket.domain.Message;
import com.hasee.websocket.domain.dto.MessageDto;
import com.hasee.websocket.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Consumer {

    private static final String orderTopic = "${topic.name}";

    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final MessageService messageService;

    private final WebSocketController webSocketController;

    @Autowired
    public Consumer(ObjectMapper objectMapper, ModelMapper modelMapper, MessageService messageService, WebSocketController webSocketController) {
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.messageService = messageService;
        this.webSocketController = webSocketController;
    }

    @KafkaListener(topics = orderTopic)
    public void consumeMessage(String message) throws JsonProcessingException {
        log.info("message consumed {}", message);

        MessageDto messageDto = objectMapper.readValue(message, MessageDto.class);
        Message messageConvert = modelMapper.map(messageDto, Message.class);

        String value = messageConvert.getItem() + "@" + messageConvert.getAmount();
        webSocketController.sendMessage(value);
//        messageService.persistFoodOrder(messageConvert);
    }

}
