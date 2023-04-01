package son.vu.producer.service.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import son.vu.avro.domain.SaleDetailRecord;
import son.vu.avro.domain.SaleReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class Producer {

    @Value("${topic.name}")
    private String topic;

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, SaleReport> userKafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, SaleReport> userKafkaTemplate, ObjectMapper objectMapper) {
        this.userKafkaTemplate = userKafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public String sendMessage(SaleReport saleReport) {
        Date dte=new Date();
        long milliSeconds = dte.getTime();
        ProducerRecord<String, SaleReport> producerRecord = new ProducerRecord<>(topic, milliSeconds + "", saleReport);
        userKafkaTemplate.send(producerRecord);
        return "message sent";

    }
}
