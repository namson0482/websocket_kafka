package son.vu.websocket.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import son.vu.websocket.controller.WebSocketController;
import son.vu.websocket.domain.Message;
import son.vu.websocket.domain.dto.MessageDto;
import son.vu.websocket.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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

    private final WebSocketController webSocketController;

    @Autowired
    public Consumer(ObjectMapper objectMapper, ModelMapper modelMapper, MessageService messageService, WebSocketController webSocketController) {
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.messageService = messageService;
        this.webSocketController = webSocketController;
    }

    private String proceedMessage(String[] arrayValues) {

        HashMap<String, Product> map = new HashMap();
        for(int i=1;i<arrayValues.length;i++) {
            String []items = arrayValues[i].split("\\|");
            String key = items[2].toLowerCase();
            if(map.containsKey(key)) {
                Product product = map.get(key) ;
                product.setQuantity(product.getQuantity() + Integer.parseInt(items[3]));

                String tempMoney = items[4].replaceAll("[\\.\\D]", "");
                BigDecimal temp = product.getMoney();
                temp.add(new BigDecimal(Integer.parseInt(tempMoney)));
            } else {
                Product product = new Product();
                product.setName(items[2]);
                product.setQuantity(Integer.parseInt(items[3]));
                String tempMoney = items[4].replaceAll("[\\.\\D]", "");
                BigDecimal money = new BigDecimal(Integer.parseInt(tempMoney));
                product.setMoney(money);
                map.put(items[2].toLowerCase(), product);
            }
        }

        String result = "";

        for (Map.Entry<String, Product> entry : map.entrySet()) {
            String key = entry.getKey();
            Product product = entry.getValue();
            String line = key + "$" + product.getQuantity() + "$" + product.getMoney().toString();
            if(result.equals("")) {
                result += line;
            } else {
                result += "@" + line ;
            }


        }

        return result;
    }

    @KafkaListener(topics = orderTopic)
    public void consumeMessage(String message) throws JsonProcessingException {
//        log.info("message consumed {}", message);

        MessageDto messageDto = objectMapper.readValue(message, MessageDto.class);
        Message messageConvert = modelMapper.map(messageDto, Message.class);


        String value = messageConvert.getItem() + "%" + messageConvert.getAmount();

        String []arrayValues = messageConvert.getItem().split("\n");
        log.info("Total items: {}", arrayValues.length );

        String result = proceedMessage(arrayValues);
        log.info(result);

        webSocketController.sendMessage(result);
//        messageService.persistFoodOrder(messageConvert);
    }

}

@Data
class Product {
    String name;
    int quantity;
    BigDecimal money;
}
