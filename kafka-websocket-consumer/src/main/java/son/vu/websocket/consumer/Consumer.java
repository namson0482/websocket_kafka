package son.vu.websocket.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.Data;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import son.vu.avro.domain.Customer;
import son.vu.avro.domain.SaleDetail;
import son.vu.avro.domain.SaleDetailRecord;
import son.vu.avro.domain.SaleReport;
import son.vu.websocket.config.ApplicationBean;
import son.vu.websocket.controller.WebSocketController;
import son.vu.websocket.domain.Message;
import son.vu.websocket.domain.dto.MessageDto;
import son.vu.websocket.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
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
    public void consumeMessage(SaleReport saleReport) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        FileWriter writer = new FileWriter("");

        var mappingStrategy =  new ColumnPositionMappingStrategy();

        mappingStrategy.setType(SaleDetailRecord.class);
        String[] columns = new String[]
                { "SalesDate", "StoreName", "ProductName", "SalesUnits", "SalesRevenue" };
        mappingStrategy.setColumnMapping(columns);
        var builder=  new StatefulBeanToCsvBuilder(writer);

        StatefulBeanToCsv beanWriter =
                builder.withMappingStrategy(mappingStrategy).build();

        // Write list to StatefulBeanToCsv object
        beanWriter.write(saleReport.getSaleDetailList());
        log.info(saleReport.toString());
    }

}

@Data
class Product {
    String name;
    int quantity;
    BigDecimal money;
}
