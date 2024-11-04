package org.netology.ru.money_transfer_service.service;

import org.netology.ru.money_transfer_service.model.BankAccount;
import org.netology.ru.money_transfer_service.model.ErrorResponse;
import org.netology.ru.money_transfer_service.model.InputObjectForTrancfer;
import org.netology.ru.money_transfer_service.model.TransferResponse;
import org.netology.ru.money_transfer_service.repository.CreateCardList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class MoneyTransferService {
    private static final Logger logger = LoggerFactory.getLogger(MoneyTransferService.class);
    private static final Double COMMISION  = 0.01;
    private final CreateCardList repository;
    public MoneyTransferService(CreateCardList repository) {
        this.repository = repository;
    }


    public ResponseEntity<?> makeTransfer(InputObjectForTrancfer objectForTrancfer) {
        Map<Long, BankAccount> bankAccountList = repository.createBankAccountList();
        Optional<BankAccount> cardFrom = bankAccountList.values().stream()
                .filter(account -> account.getCardNumner().replaceAll("\\s+", "").equals(objectForTrancfer.getCardFromNumber().replaceAll("\\s+", "")))
                .findFirst();
        Optional<BankAccount> cardTo = bankAccountList.values().stream()
                .filter(account -> account.getCardNumner().replaceAll("\\s+", "").equals(objectForTrancfer.getCardToNumber().replaceAll("\\s+", "")))
                .findFirst();
        if (!cardFrom.isPresent() || !cardTo.isPresent()) {

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Некорректные данные карты");
            errorResponse.setId(1);
            setLogger(objectForTrancfer, "Некорректные данные карты");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        if (!cheakCardFromValidTill(cardFrom.get().getCardValidTill(), objectForTrancfer.getCardFromValidTill())) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Недействительная карта отправителя");
            errorResponse.setId(2);
            setLogger(objectForTrancfer, "Недействительная карта отправителя");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (!cheakCardFromCVV(cardFrom.get().getCVV(), objectForTrancfer.getCardFromCVV())) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Недействительная код CVC");
            errorResponse.setId(3);
            setLogger(objectForTrancfer, "Недействительная код CVC");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (!cheakAmount(cardFrom.get().getAmount().getValue(), objectForTrancfer.getAmount().getValue())) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Недостаточно средств");
            errorResponse.setId(4);
            setLogger(objectForTrancfer, "Недостаточно средств");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        setLogger(objectForTrancfer, "Перевод выполнен");
        TransferResponse response = new TransferResponse();
        response.setOperationId("op-" + System.currentTimeMillis()); // Генерация уникального ID операции
        return ResponseEntity.ok(response);
    }




    private Boolean cheakCardFromValidTill(String accountValidTill, String validTill) {
        if (!accountValidTill.equals(validTill)) {
            return false;
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        String curDate = sdf.format(date);

        String[] curDateArray = curDate.split("/");
        String[] accountDateArray = accountValidTill.split("/");

        int currentYear = Integer.parseInt(curDateArray[1]);
        int currentMonth = Integer.parseInt(curDateArray[0]);
        int accountYear = Integer.parseInt(accountDateArray[1]);
        int accountMonth = Integer.parseInt(accountDateArray[0]);

        if (accountYear > currentYear || (accountYear == currentYear && accountMonth >= currentMonth)) {
            return true;
        }

        return false;
    }


    private Boolean cheakCardFromCVV(String accountCvv, String cvv) {
        return accountCvv.equals(cvv);
    }


    private Boolean cheakAmount(Double sumMoneyOnCurd, Double monneySum) {
        return (sumMoneyOnCurd - (monneySum + (monneySum * COMMISION))) > 0;
    }

    private void setLogger(InputObjectForTrancfer objectForTrancfer, String status) {

        logger.info("Перевод: {} | {} | Сумма: {} | Комиссия: {} | Результат: {}",
                objectForTrancfer.getCardFromNumber(), objectForTrancfer.getCardToNumber(), objectForTrancfer.getAmount().getValue(), objectForTrancfer.getAmount().getValue() * COMMISION, status);

    }


}
