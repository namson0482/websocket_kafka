package son.vu.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import son.vu.producer.domain.MessageContent;
import son.vu.producer.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/order")
public class MessageController {

    private final MessageService msgService;

    @Autowired
    public MessageController(MessageService msgService) {
        this.msgService = msgService;
    }

    @PostMapping
    public String createFoodOrder(@RequestBody MessageContent messageContent) throws JsonProcessingException {
        log.info("create food order request received");
        return msgService.createMessageOrder(messageContent);
    }
}
