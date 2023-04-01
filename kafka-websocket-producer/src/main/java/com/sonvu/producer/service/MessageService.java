package com.sonvu.producer.service;

import com.sonvu.avro.domain.SaleReport;
import com.sonvu.producer.service.producer.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MessageService {

    private final Producer producer;

    @Autowired
    public MessageService(Producer producer) {
        this.producer = producer;
    }

    public String createMessageOrder(SaleReport saleReport) {
        return producer.sendMessage(saleReport);
    }
}
