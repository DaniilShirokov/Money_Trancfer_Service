package org.netology.ru.money_transfer_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.netology.ru.money_transfer_service.model.*;
import org.netology.ru.money_transfer_service.repository.CreateCardList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MoneyTransferServiceTest {
    @Mock
    private CreateCardList repository;

    @InjectMocks
    private MoneyTransferService moneyTransferService;

    private BankAccount senderAccount;
    private BankAccount receiverAccount;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        senderAccount = new BankAccount("1234 5678 9012 3456", "123", "+78005553535", new Amount(1000.0, "RUB"), "12/24");
        receiverAccount = new BankAccount("6543 2109 8765 4321", "321", "+78005553536", new Amount(100.0, "RUB"), "05/25");

        ConcurrentHashMap<Long, BankAccount> bankAccounts = new ConcurrentHashMap<>();
        bankAccounts.put(1L, senderAccount);
        bankAccounts.put(2L, receiverAccount);

        when(repository.createBankAccountList()).thenReturn(bankAccounts);

        moneyTransferService = new MoneyTransferService(repository);
    }

    @Test
    void testMakeTransfer_Success() {
        InputObjectForTrancfer transferObject = new InputObjectForTrancfer("1234 5678 9012 3456", "12/24", "123", "6543 2109 8765 4321", new Amount(500.0, "RUB"));

        ResponseEntity<?> response = moneyTransferService.makeTransfer(transferObject);

        assertEquals(200, response.getStatusCodeValue());
        TransferResponse transferResponse = (TransferResponse) response.getBody();
        assertNotNull(transferResponse);
        assertNotNull(transferResponse.getOperationId());
    }

    @Test
    public void testMakeTransfer_InvalidCardFrom() {
        InputObjectForTrancfer transferInput = new InputObjectForTrancfer("invalidCard",
                receiverAccount.getCardNumner(),
                "12/25",
                "123",
                new Amount(100.0, "RUB"));

        ResponseEntity<?> response = moneyTransferService.makeTransfer(transferInput);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


    @Test
    public void testCheckCardFromValidTill_ValidDate() {
        String accountValidTill = "12/25";
        String validTill = "12/25";
        assertTrue(moneyTransferService.checkCardFromValidTill(accountValidTill, validTill));
    }

    @Test
    public void testCheckCardFromValidTill_InvalidDate() {
        String accountValidTill = "12/24";
        String validTill = "12/25";
        assertFalse(moneyTransferService.checkCardFromValidTill(accountValidTill, validTill));
    }

    @Test
    public void testCheckCardFromCVV_ValidCVV() {
        String accountCvv = "123";
        String cvv = "123";
        assertTrue(moneyTransferService.checkCardFromCVV(accountCvv, cvv));
    }

    @Test
    public void testCheckCardFromCVV_InvalidCVV() {
        String accountCvv = "123";
        String cvv = "321";
        assertFalse(moneyTransferService.checkCardFromCVV(accountCvv, cvv));
    }

    @Test
    public void testCheckAmount_SufficientFunds() {
        Double availableAmount = 100.0;
        Double transferAmount = 50.0;
        assertTrue(moneyTransferService.checkAmount(availableAmount, transferAmount));
    }

    @Test
    public void testCheckAmount_InsufficientFunds() {
        Double availableAmount = 40.0;
        Double transferAmount = 50.0;
        assertFalse(moneyTransferService.checkAmount(availableAmount, transferAmount));
    }
}
