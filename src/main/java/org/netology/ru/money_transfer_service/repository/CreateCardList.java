package org.netology.ru.money_transfer_service.repository;

import org.netology.ru.money_transfer_service.model.BankAccount;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class CreateCardList {

    public ConcurrentHashMap<Long, BankAccount> createBankAccountList()  {
        var mapObject = new ConcurrentHashMap<Long, BankAccount>();
        mapObject.put(1L,new BankAccount("4300111122223333", "123","+78005553535", "dollar", 10000 , "07/30" ));
        mapObject.put(2L,new BankAccount("4300999988887777", "987","+78005553536", "dollar", 0 , "05/25" ));
        return mapObject;
    }
}
