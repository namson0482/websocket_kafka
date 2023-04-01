package com.sonvu.producer.tasks;

import com.opencsv.bean.CsvToBeanBuilder;
import com.sonvu.avro.domain.SaleDetailRecord;
import com.sonvu.avro.domain.SaleReport;
import com.sonvu.producer.config.AppConfig;
import com.sonvu.producer.domain.SalesDetailBean;
import com.sonvu.producer.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class SendMessageTask {
    private final MessageService messageOrderService;

    final AppConfig appConfig;

    private final String TEMP_FILE = System.getProperty("user.home") + "/websocket.temp";

    private String startDate;

    private String endDate;

    @Value("${app.interval-time}")
    private int intervalTime ;

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
        SaleReport saleReport = readFilePSV();
        messageOrderService.createMessageOrder(saleReport);
        log.info("{} millisecond read data one time and successfully", intervalTime);
    }
}
