package son.vu.producer.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import son.vu.avro.domain.SaleDetail;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
public class AppConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    public String bootstrapServers;

    @Value("${app.data-file}")
    public String dataFile;


    @Bean
    public ProducerFactory<String, SaleDetail> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put("acks", "all");
        configProps.put("retries", "10");
        configProps.put("key.serializer", StringSerializer.class.getName());
        configProps.put("value.serializer", KafkaAvroSerializer.class.getName());
        configProps.put("schema.registry.url", "http://127.0.0.1:8081");
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public KafkaTemplate<String, SaleDetail> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

//    @Bean
//    public NewTopic topic() {
//        return TopicBuilder
//                .name("t.message.order")
//                .partitions(1)
//                .replicas(1)
//                .build();
//    }

}
