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
    Integer moneyValue;
    String currency;
    String phoneNumber;

    public BankAccount(String cardNumner,String cvv, String phoneNumber, String currency, Integer moneyValue, String cardValidTill) {
        this.cardNumner = cardNumner;
        this.phoneNumber = phoneNumber;
        this.CVV = cvv;
        this.currency = currency;
        this.moneyValue = moneyValue;
        this.cardValidTill = cardValidTill;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "cardNumner='" + cardNumner + '\'' +
                ", cardValidTill='" + cardValidTill + '\'' +
                ", CVV='" + CVV + '\'' +
                ", moneyValue=" + moneyValue +
                ", currency='" + currency + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
