package son.vu.producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import son.vu.avro.domain.SaleDetail;
import son.vu.producer.domain.MessageContent;
import son.vu.producer.service.producer.Producer;

@Slf4j
@Service
public class MessageService {

    private final Producer producer;

    @Autowired
    public MessageService(Producer producer) {
        this.producer = producer;
    }

    public String createMessageOrder(SaleDetail saleDetail) throws JsonProcessingException {
        return producer.sendMessage(saleDetail);
    }
}
