package son.vu.producer.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import son.vu.avro.domain.SaleDetail;
import son.vu.avro.domain.SaleDetailRecord;
import son.vu.avro.domain.SaleReport;
import son.vu.producer.config.AppConfig;
import son.vu.producer.domain.MessageContent;
import son.vu.producer.domain.SalesDetailBean;
import son.vu.producer.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class SendMessageTask {
    private final MessageService messageOrderService;

    final AppConfig appConfig;

    private final String TEMP_FILE = System.getProperty("user.home") + "/websocket.temp";

    private String startDate;

    private String endDate;

    @Autowired
    public SendMessageTask(MessageService messageOrderService, AppConfig appConfig) {
        this.messageOrderService = messageOrderService;
        this.appConfig = appConfig;
    }


    public String getFileName() {
        String result = "";
        try {
            Path path = Paths.get(TEMP_FILE);
            int month = 10;
            if (Files.exists(path)) {
                FileInputStream fstream = new FileInputStream(TEMP_FILE);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;

                while ((strLine = br.readLine()) != null)   {
                    month = Integer.parseInt(strLine) + 1;
                }
                fstream.close();
                if(month == 13) {
                    month = 10;
                }
                Files.delete(path);
            }
            FileWriter myWriter = new FileWriter(TEMP_FILE);
            myWriter.write(month + "");
            myWriter.close();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
            String dateInString = "01-" + month + "-2022";
            Date date = sdf.parse(dateInString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            result = "Sales_" + "2022" + month + "01_" + "2022" + month + lastDay + ".psv";
            startDate = "2022" + month + "01";
            endDate = "2022" + month +  "" + lastDay;

        } catch (IOException e) {

        } catch (ParseException e) {

        }
        return result;
    }

    SaleReport  readFilePSV() throws IOException {
        String fileName = getFileName();
        try (
                BufferedReader reader = Files.newBufferedReader(Paths.get(appConfig.dataFile + "/" + fileName));
        ) {
            List<SalesDetailBean> beans = new CsvToBeanBuilder(reader)
                    .withType(SalesDetailBean.class)
                    .withSeparator('|')
                    .withSkipLines(1)
                    .build()
                    .parse();

            List<SaleDetailRecord> listResult = new ArrayList<>();
            for(SalesDetailBean item: beans) {
                SaleDetailRecord saleDetailRecord = SaleDetailRecord.newBuilder()
                        .setProductName(item.getProductName())
                        .setSalesDate(item.getSalesDate())
                        .setStoreName(item.getStoreName())
                        .setSalesUnits(Integer.parseInt(item.getSalesUnits()))
                        .setSalesRevenue(Float.parseFloat(item.getSalesRevenue()))
                        .build();
                listResult.add(saleDetailRecord);
            }
            SaleReport saleReport = SaleReport.newBuilder()
                    .setStartDate(startDate)
                    .setEndDate(endDate)
                    .setSaleDetailList(listResult)
                    .build();
            return saleReport;
        }

    }

    // run every 3 sec
    @Scheduled(fixedRateString = "${app.interval-time:60000}")
    public void send() throws IOException {
//        SaleReport saleReport = readFilePSV();
//        messageOrderService.createMessageOrder(saleReport);
        log.info("loop successfully");
    }
}
