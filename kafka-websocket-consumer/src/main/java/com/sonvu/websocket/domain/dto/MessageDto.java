package com.sonvu.websocket.domain.dto;

import lombok.Data;
import lombok.Value;

import java.util.List;

@Data
@Value
public class MessageDto {

    List<List<String>> products;

    String item;
    Double amount;
}
