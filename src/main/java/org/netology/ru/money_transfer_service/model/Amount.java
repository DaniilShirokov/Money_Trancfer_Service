package org.netology.ru.money_transfer_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Amount {
    private Integer value;
    private String currency;

    public Amount(Integer value, String currency) {
        this.value = value;
        this.currency = currency;
    }
}
