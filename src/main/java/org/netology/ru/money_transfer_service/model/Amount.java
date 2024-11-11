package org.netology.ru.money_transfer_service.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Amount {
    private Double value;
    private String currency;

    public Amount(Double value, String currency) {
        this.value = value;
        this.currency = currency;
    }
}
