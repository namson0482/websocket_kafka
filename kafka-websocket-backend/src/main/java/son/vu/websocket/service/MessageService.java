package son.vu.websocket.service;

import son.vu.websocket.domain.Message;
import son.vu.websocket.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void persistFoodOrder(Message message) {
        Message persistedMessage = messageRepository.save(message);
        log.info("food order persisted {}", persistedMessage);
    }

}