package son.vu.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import son.vu.avro.domain.SaleReport;
import son.vu.producer.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
    public String kafkaMessage(@RequestBody SaleReport message) throws JsonProcessingException {
        msgService.createMessageOrder(message);
        return "Success";
    }


}
