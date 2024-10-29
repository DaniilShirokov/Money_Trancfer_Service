package org.netology.ru.money_transfer_service.repository;

import org.netology.ru.money_transfer_service.model.Amount;
import org.netology.ru.money_transfer_service.model.BankAccount;

public interface TrancferInterface {
    Boolean cheakCardNumber(String accountCardNumber, String cardNumber);
    Boolean cheakCardFromValidTill(String accountValidTill, String ValidTill);
    Boolean cheakCardFromCVV(String accountCvv, String cvv);
    Boolean cheakAmount(int sumMoneyOnCurd, int monneySum);
}
