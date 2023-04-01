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

//    public String sendMessage(MessageContent messageContent) throws JsonProcessingException {
//        String orderAsMessage = objectMapper.writeValueAsString(messageContent);
//        kafkaTemplate.send(orderTopic, orderAsMessage);
//        log.info("============================= Message order produced =============================");
//        return "message sent";
//    }

    public String sendMessage(SaleReport saleReport) throws JsonProcessingException {
        Date dte=new Date();
        long milliSeconds = dte.getTime();

        List<SaleDetailRecord> listSaleDetailRecord = new ArrayList();
        SaleDetailRecord saleDetailRecord = SaleDetailRecord.newBuilder()
                .setSalesDate("12102022")
                .setSalesUnits(1)
                .setSalesRevenue(2000f)
                .setProductName("Tivi")
                .setStoreName("Outlet one")
                .build();

        listSaleDetailRecord.add(saleDetailRecord);


        SaleReport value = SaleReport.newBuilder()
                .setEndDate("10102022")
                .setStartDate("12102022")
                .setSaleDetailList(listSaleDetailRecord)
                .build();

        ProducerRecord<String, SaleReport> producerRecord = new ProducerRecord<>(topic, milliSeconds + "", value);
        userKafkaTemplate.send(producerRecord);
        return "message sent";

    }
}
