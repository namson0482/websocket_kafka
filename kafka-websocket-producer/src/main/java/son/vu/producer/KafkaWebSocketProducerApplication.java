package son.vu.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaWebSocketProducerApplication {

	private static final Logger logger = LoggerFactory.getLogger(KafkaWebSocketProducerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(KafkaWebSocketProducerApplication.class, args);
		logger.info("Running...");
	}

}
