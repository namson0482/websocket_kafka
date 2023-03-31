package son.vu.websocket.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import son.vu.avro.domain.SaleReport;
import son.vu.websocket.config.ApplicationBean;
import son.vu.websocket.controller.WebSocketController;
import son.vu.websocket.service.MessageService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
    public void consumeMessage(SaleReport saleReport) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        messageService.persistMessage(saleReport);
        log.info(TOTAL_ITEM_RECEIVED);
    }

}

@Data
class Product {
    String name;
    int quantity;
    BigDecimal money;
}
