package org.netology.ru.money_transfer_service.repository;

import org.netology.ru.money_transfer_service.model.Amount;
import org.netology.ru.money_transfer_service.model.BankAccount;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CreateCardList {

    public ConcurrentHashMap<Long, BankAccount> createBankAccountList()  {
        var mapObject = new ConcurrentHashMap<Long, BankAccount>();
        mapObject.put(1L,new BankAccount("4300111122223333", "123","+78005553535", new Amount(1000.0, "RUB") , "12/24" ));
        mapObject.put(2L,new BankAccount("4300999988887777", "987","+78005553536", new Amount(100.0, "RUB") , "05/25" ));
        return mapObject;
    }
}
