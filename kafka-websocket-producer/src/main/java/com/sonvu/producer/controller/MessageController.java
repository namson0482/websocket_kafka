package com.sonvu.producer.controller;

import com.sonvu.avro.domain.SaleReport;
import com.sonvu.producer.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService msgService;

    @Autowired
    public MessageController(MessageService msgService) {
        this.msgService = msgService;
    }

    @PostMapping(value = "/sale")
    public String kafkaMessage(@RequestBody SaleReport message) {
        log.info(message.toString());
        msgService.createMessageOrder(message);
        return "Success";
    }


}
