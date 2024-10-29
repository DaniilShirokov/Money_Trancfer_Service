package org.netology.ru.money_transfer_service.service;

import org.netology.ru.money_transfer_service.model.BankAccount;
import org.netology.ru.money_transfer_service.model.ObjectForTrancfer;
import org.netology.ru.money_transfer_service.repository.TrancferRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MoneyTransferService {

    private final TrancferRepository repository;

    public MoneyTransferService(TrancferRepository repository) {
        this.repository = repository;
    }


    public void makeTransfer(ObjectForTrancfer objectForTrancfer) {
        Map<Long, BankAccount> bankAccountList = repository.getBankAccointList();
        Optional<BankAccount> foundSendingAccount = bankAccountList.values().stream()
                .filter(account -> account.getCardNumner().replaceAll("\\s+", "").equals(objectForTrancfer.getCardFromNumber().replaceAll("\\s+", "")))
                .findFirst();
        Optional<BankAccount> foundReceivingAccount = bankAccountList.values().stream()
                .filter(account -> account.getCardNumner().replaceAll("\\s+", "").equals(objectForTrancfer.getCardToNumber().replaceAll("\\s+", "")))
                .findFirst();
        if (foundSendingAccount.isPresent()) {
            System.out.println("Найден счет: " + objectForTrancfer.getCardFromNumber());
            System.out.println(repository.cheakCardNumber(foundSendingAccount.get().getCardNumner(),objectForTrancfer.getCardFromNumber()));
            System.out.println(repository.cheakCardFromValidTill(foundSendingAccount.get().getCardValidTill(), objectForTrancfer.getCardFromValidTill()));
            System.out.println(repository.cheakCardFromCVV(foundSendingAccount.get().getCVV(), objectForTrancfer.getCardFromCVV()));
            System.out.println(repository.cheakAmount(foundSendingAccount.get().getMoneyValue(),objectForTrancfer.getAmount().getValue()));

        } else {
            System.out.println("Счет не найден.");

        }
    }
}
