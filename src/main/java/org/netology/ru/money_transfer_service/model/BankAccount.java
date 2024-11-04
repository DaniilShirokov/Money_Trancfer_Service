package org.netology.ru.money_transfer_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BankAccount {
    String cardNumner;
    String cardValidTill;
    String CVV;
    Amount amount;
    String phoneNumber;

    public BankAccount(String cardNumner,String cvv, String phoneNumber, Amount amount, String cardValidTill) {
        this.cardNumner = cardNumner;
        this.phoneNumber = phoneNumber;
        this.CVV = cvv;
        this.amount = amount;
        this.cardValidTill = cardValidTill;
    }

}
