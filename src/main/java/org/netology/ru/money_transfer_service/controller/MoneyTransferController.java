package org.netology.ru.money_transfer_service.controller;

import org.netology.ru.money_transfer_service.model.ConfirmOperationObject;
import org.netology.ru.money_transfer_service.model.InputObjectForTrancfer;
import org.netology.ru.money_transfer_service.service.MoneyTransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class MoneyTransferController {
    private final MoneyTransferService service;

    public MoneyTransferController(MoneyTransferService service) {
        this.service = service;
    }

    @PostMapping("/trancfer")
    public ResponseEntity<?> postTrancfer(@RequestBody InputObjectForTrancfer objectForTrancfer)  {
          return service.makeTransfer(objectForTrancfer);

    }

    @PostMapping("/confirmOperation")
    public ResponseEntity<?> confirmOperation (@RequestBody ConfirmOperationObject confirmOperationObject) {
        return service.confirmOperation(confirmOperationObject);
    }

}
