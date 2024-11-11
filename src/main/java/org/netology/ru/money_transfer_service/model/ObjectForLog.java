package org.netology.ru.money_transfer_service.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Data
public class ObjectForLog {
    private String cardFrom;
    private String cardTo;
    private BigDecimal amount;
    private BigDecimal commision;
    private String resultoperation;
}
