package son.vu.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import son.vu.avro.domain.SaleReport;
import son.vu.producer.service.MessageService;

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
