package son.vu.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

@Slf4j
@Service
public class MessageService {


    @Value("${app.data-file}")
    public String dataFilePath;

    private static final String HEADER_FILE = "\"SalesDate\"|\"StoreName\"|\"ProductName\"|\"SalesUnits\"|\"SalesRevenue\"";

    public void persistMessage(String []arrayValues) {


        Date date = new Date();
        long timeMilli = date.getTime();
        String fileName = dataFilePath + "/" + "Sales_" + timeMilli + ".psv";

        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.write(HEADER_FILE + "\n");
            for(String item: arrayValues) {
                writer.write(item + "\n");
            }
            writer.close();
            log.info("Write file done!");
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }

    }

}
