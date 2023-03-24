package son.vu.websocket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import son.vu.websocket.domain.MessageContent;
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

    private final WebSocketController webSocketController;

    @Autowired
    MessageController(WebSocketController webSocketController) {
        this.webSocketController = webSocketController;
    }

    @PostMapping
    public String createFoodOrder(@RequestBody MessageContent messageContent) throws JsonProcessingException {
        log.info("create food order request received");
        String value = messageContent.getItem() + "@" + messageContent.getAmount();
        webSocketController.sendMessage(value);
        return value;
    }
}
