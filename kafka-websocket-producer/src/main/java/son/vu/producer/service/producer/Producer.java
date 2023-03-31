package son.vu.producer.service.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import son.vu.avro.domain.SaleDetail;
import son.vu.producer.domain.MessageContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class Producer {

    @Value("${topic.name}")
    private String topic;

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, SaleDetail> kafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, SaleDetail> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

//    public String sendMessage(MessageContent messageContent) throws JsonProcessingException {
//        String orderAsMessage = objectMapper.writeValueAsString(messageContent);
//        kafkaTemplate.send(orderTopic, orderAsMessage);
//        log.info("============================= Message order produced =============================");
//        return "message sent";
//    }

    public String sendMessage(SaleDetail saleDetail) throws JsonProcessingException {
        Date dte=new Date();
        long milliSeconds = dte.getTime();

        ProducerRecord<String, SaleDetail> producerRecord = new ProducerRecord<>(topic, milliSeconds + "", saleDetail);
        kafkaTemplate.send(producerRecord);
        return "message sent";

    }
}
