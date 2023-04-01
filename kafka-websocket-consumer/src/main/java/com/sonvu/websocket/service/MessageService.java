package com.sonvu.websocket.service;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sonvu.avro.domain.SaleDetailRecord;
import com.sonvu.avro.domain.SaleReport;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
public class MessageService {


    @Value("${app.data-file}")
    public String dataFilePath;

    private static final String COMMA_DELIMITER = "|";
    private static final String FILE_HEADER = "\"SalesDate\"|\"StoreName\"|\"ProductName\"|\"SalesUnits\"|\"SalesRevenue\"";

    private static final String NEW_LINE_SEPARATOR = "\n";

    public void persistMessage(SaleReport saleReport) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {

        Date date = new Date();
        long timeMilli = date.getTime();
        String fileName = dataFilePath + "/" + "Sales_" + timeMilli + ".psv";

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.append(FILE_HEADER.toString());
            fileWriter.append(NEW_LINE_SEPARATOR);
            for(SaleDetailRecord saleDetailRecord: saleReport.getSaleDetailList()) {
                fileWriter.append(String.valueOf(saleDetailRecord.getSalesDate()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(saleDetailRecord.getStoreName()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(saleDetailRecord.getProductName()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(saleDetailRecord.getSalesUnits()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(saleDetailRecord.getSalesRevenue()));
                fileWriter.append(NEW_LINE_SEPARATOR);
            }
        } catch (Exception e) {
            log.error("Error in CsvFileWriter !!!");
        } finally {

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }

        }
    }

}
