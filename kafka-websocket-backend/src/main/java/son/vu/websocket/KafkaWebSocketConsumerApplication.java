package son.vu.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaWebSocketConsumerApplication {

	private static final Logger logger = LoggerFactory.getLogger(KafkaWebSocketConsumerApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(KafkaWebSocketConsumerApplication.class, args);
		logger.info("Running...");
	}

}
