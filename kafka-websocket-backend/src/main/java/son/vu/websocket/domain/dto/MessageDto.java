package son.vu.websocket.domain.dto;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class MessageDto {
    String item;
    Double amount;
}
