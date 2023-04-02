package com.sonvu.websocket.consumer;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.sonvu.avro.domain.SaleDetailRecord;
import com.sonvu.avro.domain.SaleReport;
import com.sonvu.websocket.config.ApplicationBean;
import com.sonvu.websocket.controller.WebSocketController;
import com.sonvu.websocket.domain.Product;
import com.sonvu.websocket.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class Consumer {

    private final MessageService messageService;

    static final String TOTAL_ITEM_RECEIVED = "Consumed done and {} records and money   {} VNĐ";
    private final WebSocketController webSocketController;

    private final static Locale VN = new Locale("vi", "VN");

    @Autowired
    ApplicationBean applicationBean;

    public Consumer(MessageService messageService, WebSocketController webSocketController) {
        this.messageService = messageService;
        this.webSocketController = webSocketController;
    }

    public static String getDigit(String quote, Locale locale) {
        char decimalSeparator;
        if (locale == null) {
            decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();
        } else {
            decimalSeparator = new DecimalFormatSymbols(locale).getDecimalSeparator();
        }

        String regex = "[^0-9" + decimalSeparator + "]";
        String valueOnlyDigit = quote.replaceAll(regex, "");
        try {
            return valueOnlyDigit;
        } catch (ArithmeticException | NumberFormatException e) {
            return null;
        }
    }

    public static String addTwoNumbers(final String n1, final String n2) {
        StringBuilder sb = new StringBuilder();
        int carry = 0;
        byte[] nb1;
        byte[] nb2;
        if (n1.length() > n2.length()) {
            nb1 = n1.getBytes();
            nb2 = n2.getBytes();
        } else {
            nb2 = n1.getBytes();
            nb1 = n2.getBytes();
        }

        int maxLen = n1.length() >= n2.length() ? n1.length() : n2.length();
        for (int i = 1; i <= maxLen; i++) {
            int a = nb1.length - i >= 0 ? nb1[nb1.length - i] - 48 : 0;
            int b = nb2.length - i >= 0 ? nb2[nb2.length - i] - 48 : 0;
            int result = a + b + carry;

            if (result >= 10) {
                carry = 1;
                result = result - 10;
            } else {
                carry = 0;
            }
            sb.insert(0, result);
        }
        if (carry > 0) {
            sb.insert(0, carry);
        }
        return sb.toString();
    }
    private Map<String, Product> sortOutSaleDetailRecord(SaleReport saleReport) {

        Map<String, Product> map = new HashMap();
        List<SaleDetailRecord> listSaleDetailRecord = saleReport.getSaleDetailList();
        for (SaleDetailRecord record : listSaleDetailRecord) {
            String revenue = getDigit(record.getSalesRevenue().toString(), VN);
            String key = record.getProductName().toString().toLowerCase();

            if (!map.containsKey(key)) {
                Product product = new Product(record.getProductName().toString(), record.getSalesUnits(), revenue);
                map.put(key, product);
            } else {
                Product product = map.get(key);
                String totalMoney = addTwoNumbers(revenue, product.getTotalMoney());
                product.setTotalMoney(totalMoney);
                product.setSalesUnit(product.getSalesUnit() + record.getSalesUnits());
            }
        }
        return map;
    }

    private String proceedMessage(SaleReport saleReport) {

        Map<String, Product> map = sortOutSaleDetailRecord(saleReport);
        String result = "";
        int totalLength = 35;
        String sTempTotalMoney = "0";
        for (Map.Entry<String, Product> item : map.entrySet()) {
            Product product = item.getValue();
            String sTempName = product.getName();
            while (sTempName.length() < totalLength) {
                sTempName += " ";
            }
            String sTempSalesUnit = item.getValue().getSalesUnit() + "";
            while (sTempSalesUnit.length() < totalLength - 30) {
                sTempSalesUnit += " ";
            }
            String tempMoneyWithDilimiter = addDelimiter(product.getTotalMoney(), ".");
            String tempMoneyFrontEnd = tempMoneyWithDilimiter;
            while(tempMoneyWithDilimiter.length() < 17) {
                tempMoneyWithDilimiter += " ";
            }

            String line = product.getName() + "$" + product.getSalesUnit() + "$" + tempMoneyFrontEnd + "VNĐ";
            if (result.equals("")) {
                result += line;
            } else {
                result += "@" + line;
            }
            sTempTotalMoney = addTwoNumbers(sTempTotalMoney, product.getTotalMoney());
            log.info("{} {} {}", sTempName, sTempSalesUnit, tempMoneyWithDilimiter + "VNĐ");
        }
        applicationBean.setMoney(sTempTotalMoney);
        return result;

    }

    private static String addDelimiter(String original, String separator) {
        String result = "";
        int k = 0;
        for (int i = original.length(); i > 0; i--) {
            result = original.charAt(i - 1) + result;
            k++;
            if (k % 3 == 0 && i > 1) {
                result = separator + result;
                k = 0;
            }
        }
        return result;
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessage(SaleReport saleReport) {
        String res = proceedMessage(saleReport);
        applicationBean.setData(res);
        webSocketController.sendMessage(res);
        try {
            messageService.persistMessage(saleReport);
            log.info(TOTAL_ITEM_RECEIVED, saleReport.getSaleDetailList().size(), addDelimiter(applicationBean.getMoney(), "."));
            int k = 0;
            String stemp = "";
            while (k++ < 70) stemp += "=";
            log.info(stemp);
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException | IOException e) {
            log.error(e.getMessage());
        }
    }
}
