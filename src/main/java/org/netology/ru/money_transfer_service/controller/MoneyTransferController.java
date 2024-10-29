package org.netology.ru.money_transfer_service.controller;

import org.netology.ru.money_transfer_service.model.BankAccount;
import org.netology.ru.money_transfer_service.model.ObjectForTrancfer;
import org.netology.ru.money_transfer_service.service.MoneyTransferService;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class MoneyTransferController {
    private final MoneyTransferService service;

    public MoneyTransferController(MoneyTransferService service) {
        this.service = service;
    }

    @PostMapping("/trancfer")
        public void postTrancfer(@RequestBody ObjectForTrancfer objectForTrancfer)  {
            service.makeTransfer(objectForTrancfer);
        }
}
