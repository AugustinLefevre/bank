package com.itinov.bank.controllers;

import com.itinov.bank.entities.*;
import com.itinov.bank.requests.TransactionRequest;
import com.itinov.bank.security.CustomUserDetails;
import com.itinov.bank.services.TransactionService;
import jakarta.transaction.Transactional;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class TransactionControllerTest {
    @Autowired
    private TransactionController transactionController;

    private final CustomUserDetails customUserDetails;
    private final User user;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountController accountService;

    public TransactionControllerTest() {
        user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setPassword("hashed_password");

        this.customUserDetails = new CustomUserDetails(user);
    }

    @Test
    void testMakeDepositFromNullThenAccepted(){
        LocalDateTime now = LocalDateTime.now();
        TransactionRequest request = new TransactionRequest(now, new BigDecimal("10.1"), TransactionType.DEPOSIT, user.getUsername(),null, 2L);
        ResponseEntity actual = transactionController.makeTransaction(customUserDetails, request);
        Assert.assertEquals(HttpStatus.OK, actual.getStatusCode());
        List<Account> accounts = accountService.getUserAccounts(customUserDetails).getBody();
        Account account2Balance = accounts.stream().filter(account -> account.getId() == 2).findAny().orElse(null);
        Assert.assertEquals(new BigDecimal("260.85"), account2Balance.getBalance());
        Transaction expectedTransaction = Transaction.builder()
                .id(4L)
                .account(account2Balance)
                .date(now)
                .from(user.getUsername())
                .amount(new BigDecimal("10.1"))
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.APPROVED)
                .balance(new BigDecimal("260.85"))
                .message("Transaction approved")
                .build();
        Assert.assertEquals(expectedTransaction, actual.getBody());
        List<Transaction> transactions = transactionService.findAllByUserId(customUserDetails);
        Assert.assertEquals(3, transactions.size());
    }

    @Test
    void testMakeWithdrawalThenAccepted(){
        LocalDateTime now = LocalDateTime.now();
        TransactionRequest request = new TransactionRequest(now, new BigDecimal("10.1"), TransactionType.WITHDRAWAL, user.getUsername(), 2L, null);
        ResponseEntity actual = transactionController.makeTransaction(customUserDetails, request);
        Assert.assertEquals(HttpStatus.OK, actual.getStatusCode());
        List<Account> accounts = accountService.getUserAccounts(customUserDetails).getBody();
        Account account2Balance = accounts.stream().filter(account -> account.getId() == 2).findAny().orElse(null);
        Assert.assertEquals(new BigDecimal("240.65"), account2Balance.getBalance());
        Transaction expectedTransaction = Transaction.builder()
                .id(7L)
                .account(account2Balance)
                .date(now)
                .from(user.getUsername())
                .amount(new BigDecimal("10.1"))
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.APPROVED)
                .balance(new BigDecimal("240.65"))
                .message("Transaction approved")
                .build();
        Assert.assertEquals(expectedTransaction, actual.getBody());
        List<Transaction> transactions = transactionService.findAllByUserId(customUserDetails);
        Assert.assertEquals(3, transactions.size());
    }

    @Test
    void testMakeWithdrawalWithInvalidAmount(){
        LocalDateTime now = LocalDateTime.now();
        TransactionRequest request = new TransactionRequest(now, new BigDecimal("300"), TransactionType.WITHDRAWAL, user.getUsername(), 2L, null);
        ResponseEntity actual = transactionController.makeTransaction(customUserDetails, request);
        Assert.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
        List<Account> accounts = accountService.getUserAccounts(customUserDetails).getBody();
        Account account2Balance = accounts.stream().filter(account -> account.getId() == 2).findAny().orElse(null);
        Assert.assertEquals(new BigDecimal("250.75"), account2Balance.getBalance());
        Transaction expectedTransaction = Transaction.builder()
                .id(1L)
                .account(account2Balance)
                .date(now)
                .from(user.getUsername())
                .amount(new BigDecimal("300"))
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.DENIED)
                .balance(new BigDecimal("250.75"))
                .message("Transaction amount is greater than the account balance")
                .build();
        Assert.assertEquals(expectedTransaction, actual.getBody());
        List<Transaction> transactions = transactionService.findAllByUserId(customUserDetails);
        Assert.assertEquals(3, transactions.size());
    }

    @Test
    void testMakeTransfer(){
        LocalDateTime now = LocalDateTime.now();
        TransactionRequest request = new TransactionRequest(now, new BigDecimal("10.1"), TransactionType.TRANSFER, user.getUsername(), 1L, 2L);
        ResponseEntity actual = transactionController.makeTransaction(customUserDetails, request);
        Assert.assertEquals(HttpStatus.OK, actual.getStatusCode());
        List<Account> accounts = accountService.getUserAccounts(customUserDetails).getBody();
        Account account1Balance = accounts.stream().filter(account -> account.getId() == 1).findAny().orElse(null);
        Account account2Balance = accounts.stream().filter(account -> account.getId() == 2).findAny().orElse(null);
        Assert.assertEquals(new BigDecimal("990.40"), account1Balance.getBalance());
        Assert.assertEquals(new BigDecimal("260.85"), account2Balance.getBalance());
        Transaction expectedTransaction = Transaction.builder()
                .id(2L)
                .account(account1Balance)
                .date(now)
                .from(user.getUsername())
                .amount(new BigDecimal("10.1"))
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.APPROVED)
                .balance(new BigDecimal("990.40"))
                .message("Transaction approved")
                .build();
        Assert.assertEquals(expectedTransaction, actual.getBody());
        List<Transaction> transactions = transactionService.findAllByUserId(customUserDetails);
        Assert.assertEquals(4, transactions.size());
    }

    @Test
    void testMakeTransferFromAccountWithInvalidAmount(){
        LocalDateTime now = LocalDateTime.now();
        TransactionRequest request = new TransactionRequest(now, new BigDecimal("2000"), TransactionType.TRANSFER, user.getUsername(), 1L, 2L);

        ResponseEntity actual = transactionController.makeTransaction(customUserDetails, request);
        Assert.assertEquals(HttpStatus.FORBIDDEN, actual.getStatusCode());
        List<Account> accounts = accountService.getUserAccounts(customUserDetails).getBody();
        Account account1Balance = accounts.stream().filter(account -> account.getId() == 1).findAny().orElse(null);
        Account account2Balance = accounts.stream().filter(account -> account.getId() == 2).findAny().orElse(null);
        Assert.assertEquals(new BigDecimal("1000.50"), account1Balance.getBalance());
        Assert.assertEquals(new BigDecimal("250.75"), account2Balance.getBalance());
        Transaction expectedTransaction = Transaction.builder()
                .id(5L)
                .account(account1Balance)
                .date(now)
                .from(user.getUsername())
                .amount(new BigDecimal("2000"))
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.DENIED)
                .balance(new BigDecimal("1000.50"))
                .message("Transaction amount is greater than the account balance")
                .build();
        Assert.assertEquals(expectedTransaction, actual.getBody());
        List<Transaction> transactions = transactionService.findAllByUserId(customUserDetails);
        Assert.assertEquals(4, transactions.size());
    }

    @Test
    public void testGetTransactions(){
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        ResponseEntity<List<Transaction>> response = transactionController.getTransactions(customUserDetails);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(2, response.getBody().size());

    }
}

