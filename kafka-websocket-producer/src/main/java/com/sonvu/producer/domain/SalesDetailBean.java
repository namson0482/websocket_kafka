package com.sonvu.producer.domain;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class SalesDetailBean {
    @CsvBindByPosition(position = 0)
    private String salesDate;

    @CsvBindByPosition(position = 1)
    private String storeName;

    @CsvBindByPosition(position = 2)
    private String productName;

    @CsvBindByPosition(position = 3)
    private String salesUnits;

    @CsvBindByPosition(position = 4)
    private String salesRevenue;
}
