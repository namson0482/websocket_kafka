package com.sonvu.websocket.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.sonvu.avro.domain.SaleDetailRecord;
import com.sonvu.avro.domain.SaleReport;
import com.sonvu.websocket.config.ApplicationBean;
import com.sonvu.websocket.controller.WebSocketController;
import com.sonvu.websocket.domain.Product;
import com.sonvu.websocket.service.MessageService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class Consumer {

    private final MessageService messageService;

    static final String TOTAL_ITEM_RECEIVED = "Just consumed a message and total items {}";
    private final WebSocketController webSocketController;

    @Autowired
    ApplicationBean applicationBean;

    @Autowired
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

    public static String currencyWithChosenLocalisation(String value, Locale locale) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        return nf.format(value);
    }

    private String proceedMessage(SaleReport saleReport) {
        Locale vn = new Locale("vi", "VN");

        Map<String, Product> map = new HashMap();
        List<SaleDetailRecord> listSaleDetailRecord = saleReport.getSaleDetailList();
        for (SaleDetailRecord record : listSaleDetailRecord) {
            String revenue = getDigit(record.getSalesRevenue().toString(), vn);
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

        String result = "";

        int totalLength = 35;
        for (Map.Entry<String, Product> item : map.entrySet()) {
            Product product = item.getValue();
            String sTempName = item.getValue().getName();
            while (sTempName.length() < totalLength) {
                sTempName += " ";
            }
            String sTempSalesUnit = item.getValue().getSalesUnit() + "";
            while (sTempSalesUnit.length() < totalLength - 30) {
                sTempSalesUnit += " ";
            }

            String line = product.getName() + "$" + product.getSalesUnit() + "$" + product.getTotalMoney();
            if (result.equals("")) {
                result += line;
            } else {
                result += "@" + line;
            }

            log.info("{} {} {}", sTempName, sTempSalesUnit, item.getValue().getTotalMoney());
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
            log.info(TOTAL_ITEM_RECEIVED, saleReport.getSaleDetailList().size());
            int k = 0;
            String stemp = "";
            while (k++ < 70) stemp += "=";
            log.info(stemp);
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException | IOException e) {
            log.error(e.getMessage());
        }
    }
}
