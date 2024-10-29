package org.netology.ru.money_transfer_service.repository;

import org.netology.ru.money_transfer_service.model.Amount;
import org.netology.ru.money_transfer_service.model.BankAccount;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Repository
public class TrancferRepository implements TrancferInterface{
    private Map<Long, BankAccount> bankAccountList  = new ConcurrentHashMap<>();


    public Map<Long, BankAccount> getBankAccointList() {
        bankAccountList = new CreateCardList().createBankAccountList();
        return bankAccountList;
    }


    @Override
    public Boolean cheakCardNumber(String accountCardNumber, String cardNumber) {
        return accountCardNumber.equals(cardNumber);
    }

    @Override
    public Boolean cheakCardFromValidTill(String accountValidTill, String validTill) {
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

    @Override
    public Boolean cheakCardFromCVV(String accountCvv, String cvv) {
        return accountCvv.equals(cvv);
    }

    @Override
    public Boolean cheakAmount(int sumMoneyOnCurd, int monneySum) {
        return (sumMoneyOnCurd - monneySum) > 0;
    }
}
