package org.netology.ru.money_transfer_service.service;

import org.netology.ru.money_transfer_service.model.*;
import org.netology.ru.money_transfer_service.repository.CreateCardList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service()
public class MoneyTransferService {
    private static final Logger logger = LoggerFactory.getLogger(MoneyTransferService.class);
    private static final Double COMMISION = 0.01;
    private final CreateCardList repository;
    Map<Long, BankAccount> bankAccountList;

    public MoneyTransferService(CreateCardList repository) {
        this.repository = repository;
        this.bankAccountList = repository.createBankAccountList();
    }

    Map<String, String> confirmOperationList = new ConcurrentHashMap<>();
    Map<String, InputObjectForTrancfer> operationDataList = new ConcurrentHashMap<>();

    public ResponseEntity<?> makeTransfer(InputObjectForTrancfer objectForTrancfer) {

        Optional<BankAccount> cardFrom = bankAccountList.values().stream()
                .filter(account -> account.getCardNumner().replaceAll("\\s+", "").equals(objectForTrancfer.getCardFromNumber().replaceAll("\\s+", "")))
                .findFirst();
        Optional<BankAccount> cardTo = bankAccountList.values().stream()
                .filter(account -> account.getCardNumner().replaceAll("\\s+", "").equals(objectForTrancfer.getCardToNumber().replaceAll("\\s+", "")))
                .findFirst();
        if (!cardFrom.isPresent() || !cardTo.isPresent()) {
            return createResponse("Некорректные данные карты",1,"Некорректные данные карты",objectForTrancfer);
        }
        if (!checkCardFromValidTill(cardFrom.get().getCardValidTill(), objectForTrancfer.getCardFromValidTill())) {
            return createResponse("Недействительная карта отправителя",2,"Недействительная карта отправителя",objectForTrancfer);
        }
        if (!checkCardFromCVV(cardFrom.get().getCVV(), objectForTrancfer.getCardFromCVV())) {
            return createResponse("Недействительная код CVC",3,"Недействительная код CVC",objectForTrancfer);
        }
        if (!checkAmount(cardFrom.get().getAmount().getValue(), objectForTrancfer.getAmount().getValue())) {
            return createResponse("Недостаточно средств",4,"Недостаточно средств",objectForTrancfer);
        }

        var setOperationId = "op-" + System.currentTimeMillis();
        String code = generateCode();
        System.out.println("На номер отправлен код подтверждения " + code);
        confirmOperationList.put(setOperationId, code);
        operationDataList.put(setOperationId, objectForTrancfer);

        System.out.println(confirmOperationList.toString());
        setLogger(objectForTrancfer, "Требуется подтверждение");
        TransferResponse response = new TransferResponse();
        response.setOperationId(setOperationId);

        return ResponseEntity.ok(response);
    }
    public ResponseEntity createResponse(String message, Integer idCode, String logerInfo, InputObjectForTrancfer objectForTrancfer) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage(message);
        errorResponse.setId(idCode);
        setLogger(objectForTrancfer, logerInfo);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    public ResponseEntity<?> confirmOperation(ConfirmOperationObject confirmOperationObject) {
        if (!confirmOperationList.get(confirmOperationObject.getOperationId()).equals(confirmOperationObject.getCode())) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Неверный индентефикатор операции");
            errorResponse.setId(5);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        var objectForOperation = operationDataList.get(confirmOperationObject.getOperationId());
        transferMoney(objectForOperation);

        var setOperationId = "op-" + System.currentTimeMillis();
        operationDataList.put(setOperationId, objectForOperation);

        setLogger(objectForOperation, "Перевод согласован, денежные средства переедены");
        TransferResponse response = new TransferResponse();
        response.setOperationId(setOperationId);
        return ResponseEntity.ok(response);
    }


    protected Boolean checkCardFromValidTill(String accountValidTill, String validTill) {
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

    protected Boolean checkCardFromCVV(String accountCvv, String cvv) {
        return accountCvv.equals(cvv);
    }


    protected Boolean checkAmount(Double sumMoneyOnCurd, Double monneySum) {
        return (sumMoneyOnCurd - (monneySum + (monneySum * COMMISION))) > 0;
    }

    private void setLogger(InputObjectForTrancfer objectForTrancfer, String status) {
        logger.info("Перевод: {} | {} | Сумма: {} | Комиссия: {} | Результат: {}",
                objectForTrancfer.getCardFromNumber(), objectForTrancfer.getCardToNumber(), objectForTrancfer.getAmount().getValue(), objectForTrancfer.getAmount().getValue() * COMMISION, status);
    }

    private String generateCode() {
        Random random = new Random();
        int number = random.nextInt(10000);
        return String.format("%04d", number);
    }

    protected Map<Long, BankAccount> transferMoney(InputObjectForTrancfer objectForTrancfer) {
        Long keyFromCard = findKey(objectForTrancfer.getCardFromNumber().replaceAll("\\s+", ""));
        Long keyToCard = findKey(objectForTrancfer.getCardFromNumber().replaceAll("\\s+", ""));

        var currentAmountFromCard = bankAccountList.get(keyFromCard).getAmount();
        var currentAmountToCard = bankAccountList.get(keyToCard).getAmount();
        bankAccountList.get(keyFromCard).setAmount(currentAmountFromCard.getValue() - objectForTrancfer.getAmount().getValue() - (objectForTrancfer.getAmount().getValue() * COMMISION));
        bankAccountList.get(keyToCard).setAmount(currentAmountToCard.getValue() + objectForTrancfer.getAmount().getValue());

        return bankAccountList;
    }

    protected Long findKey(String cardNumber) {
        Long key = null;
        for (Map.Entry<Long, BankAccount> entry : bankAccountList.entrySet()) {
            BankAccount account = entry.getValue();
            if (account.getCardNumner().equals(cardNumber)) {
                key = entry.getKey();
                break;
            }
        }
        return key;
    }
}
