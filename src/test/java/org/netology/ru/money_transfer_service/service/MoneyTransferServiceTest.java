package org.netology.ru.money_transfer_service.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.netology.ru.money_transfer_service.model.*;
import org.netology.ru.money_transfer_service.repository.CreateCardList;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MoneyTransferServiceTest {
    @Mock
    private CreateCardList repository;

    @InjectMocks
    private MoneyTransferService moneyTransferService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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

    @Test
    public void testTransferMoney() {
        // Создайте необходимые данные для теста
        Map<Long, BankAccount> bankAccounts = new ConcurrentHashMap<>();

        BankAccount fromAccount = new BankAccount("4300111122223333", "123", "+78005553535", new Amount(1000.0, "RUB"), "12/24");
        BankAccount toAccount = new BankAccount("4300999988887777", "987", "+78005553536", new Amount(100.0, "RUB"), "05/25");


        bankAccounts.put(1L, fromAccount);
        bankAccounts.put(2L, toAccount);

        when(repository.createBankAccountList()).thenReturn((ConcurrentHashMap<Long, BankAccount>) bankAccounts);

        InputObjectForTrancfer transferObject = new InputObjectForTrancfer("4300 1111 2222 3333", "12/24", "123", "4300 9999 8888 7777", new Amount(100.0, "RUB"));

        bankAccounts = moneyTransferService.transferMoney(transferObject);

        assertEquals(1000.0, bankAccounts.get(1L).getAmount().getValue(), 0.01);
        assertEquals(100.0, bankAccounts.get(2L).getAmount().getValue(), 0.01);
    }

    @Test
    public void testFindKey_CardFound() {
        Map<Long, BankAccount> bankAccounts = new ConcurrentHashMap<>();

        BankAccount account = new BankAccount("4300111122223333", "123", "+78005553535", new Amount(1000.0, "RUB"), "12/24");
        ;
        bankAccounts.put(1L, account);

        when(repository.createBankAccountList()).thenReturn((ConcurrentHashMap<Long, BankAccount>) bankAccounts);

        Long key = moneyTransferService.findKey("4300111122223333");
        assertEquals(Long.valueOf(1), key);
    }

    @Test
    public void testFindKey_CardNotFound() {
        Map<Long, BankAccount> bankAccounts = new ConcurrentHashMap<>();

        BankAccount account = new BankAccount("4300111122223333", "123", "+78005553535", new Amount(1000.0, "RUB"), "12/24");
        ;
        account.setCardNumner("1111222233334444");
        bankAccounts.put(1L, account);

        when(repository.createBankAccountList()).thenReturn((ConcurrentHashMap<Long, BankAccount>) bankAccounts);

        Long key = moneyTransferService.findKey("0000000000000000");
        assertNull(key);
    }

    @Test
    public void testMakeTransfer_SuccessfulTransfer() {

        Map<Long, BankAccount> bankAccounts = new ConcurrentHashMap<>();
        BankAccount senderAccount = new BankAccount("4300111122223333", "123", "+78005553535", new Amount(1000.0, "RUB"), "12/24");
        BankAccount receiverAccount = new BankAccount("4300999988887777", "987", "+78005553536", new Amount(100.0, "RUB"), "05/25");
        when(repository.createBankAccountList()).thenReturn((ConcurrentHashMap<Long, BankAccount>) bankAccounts);

        InputObjectForTrancfer transferObject = new InputObjectForTrancfer("4300 1111 2222 3333", "12/24", "123", "4300 9999 8888 7777", new Amount(100.0, "RUB"));

        ResponseEntity<?> response = moneyTransferService.makeTransfer(transferObject);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testMakeTransfer_InvalidCard() {
        Map<Long, BankAccount> bankAccounts = new ConcurrentHashMap<>();
        BankAccount senderAccount = new BankAccount("4300111122223333", "123", "+78005553535", new Amount(1000.0, "RUB"), "12/24");
        bankAccounts.put(1L, senderAccount);

        when(repository.createBankAccountList()).thenReturn((ConcurrentHashMap<Long, BankAccount>) bankAccounts);

        InputObjectForTrancfer transferObject = new InputObjectForTrancfer("9999 8888 7777 6666", "12/24", "123", "4300 9999 8888 7777", new Amount(100.0, "RUB"));

        ResponseEntity<?> response = moneyTransferService.makeTransfer(transferObject);

        assertEquals(400, response.getStatusCodeValue());
    }

}
