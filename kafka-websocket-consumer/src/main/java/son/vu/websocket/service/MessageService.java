package son.vu.websocket.service;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import son.vu.avro.domain.SaleDetailRecord;
import son.vu.avro.domain.SaleReport;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Service
public class MessageService {


    @Value("${app.data-file}")
    public String dataFilePath;

    private static final String HEADER_FILE = "\"SalesDate\"|\"StoreName\"|\"ProductName\"|\"SalesUnits\"|\"SalesRevenue\"";

    public void persistMessage(SaleReport saleReport) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {

        Date date = new Date();
        long timeMilli = date.getTime();
        String fileName = dataFilePath + "/" + "Sales_" + timeMilli + ".psv";

        FileWriter writer = new FileWriter(fileName);

        var mappingStrategy =  new ColumnPositionMappingStrategy();

        mappingStrategy.setType(SaleDetailRecord.class);
        String[] columns = new String[]
                { "SalesDate", "StoreName", "ProductName", "SalesUnits", "SalesRevenue" };
        mappingStrategy.setColumnMapping(columns);
        var builder=  new StatefulBeanToCsvBuilder(writer);

        StatefulBeanToCsv beanWriter =
                builder.withMappingStrategy(mappingStrategy).build();

        // Write list to StatefulBeanToCsv object
        beanWriter.write(saleReport.getSaleDetailList());


    }

}
