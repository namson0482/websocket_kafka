package son.vu.producer.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import son.vu.producer.domain.MessageContent;
import son.vu.producer.service.MessageService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class SendMessageTask {
    private final MessageService messageOrderService;

    @Autowired
    public SendMessageTask(MessageService messageOrderService) {
        this.messageOrderService = messageOrderService;
    }

    public List<List<String>> readCSV() {

        List<List<String>> records = new ArrayList<List<String>>();
        try (CSVReader csvReader = new CSVReader(new FileReader("/Users/macbook/Documents/Docs/pro_intellij/apache_kafka/deman-planing-dev-kafka/producer/data/Sales_20221001_20221031.psv"));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

//        for(List<String> item : records) {
//            System.out.println(item);
//        }
        return records;
    }

    // run every 3 sec
    @Scheduled(fixedRateString = "10000")
    public void send() throws JsonProcessingException {
        long start = System.currentTimeMillis();
        List<List<String>> records = readCSV();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        log.info("Time Elapsed: {}" , timeElapsed);

//        int leftLimit = 97; // letter 'a'
//        int rightLimit = 122; // letter 'z'
//        int targetStringLength = 10;
//        Random random = new Random();
//        StringBuilder buffer = new StringBuilder(targetStringLength);
//        for (int i = 0; i < targetStringLength; i++) {
//            int randomLimitedInt = leftLimit + (int)
//                    (random.nextFloat() * (rightLimit - leftLimit + 1));
//            buffer.append((char) randomLimitedInt);
//        }
//        String generatedString = buffer.toString();
//
//        MessageContent messageContent =  new MessageContent();
//        messageContent.setItem(generatedString);
//        int min = 200;
//        int max = 400;
//        int b = (int)(Math.random()*(max-min+1)+min);
//        messageContent.setAmount(Double.valueOf(b));

        MessageContent messageContent =  new MessageContent();

        String temp = "";
        for(List<String> list : records) {
            int index = 0;
            for(String item: list) {
                temp += item;

            }
            index++;
            if(index == 2) break;

            temp += "\n";
        }
        messageContent.setAmount(Double.valueOf(2.0));
        messageContent.setItem(temp);

        log.info("create food order request received");
        messageOrderService.createMessageOrder(messageContent);
    }
}
