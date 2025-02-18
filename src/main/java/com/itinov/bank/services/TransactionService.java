package com.itinov.bank.services;

import com.itinov.bank.entities.Account;
import com.itinov.bank.entities.Transaction;
import com.itinov.bank.entities.TransactionStatus;
import com.itinov.bank.entities.TransactionType;
import com.itinov.bank.repositories.TransactionRepository;
import com.itinov.bank.requests.TransactionRequest;
import com.itinov.bank.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repository;
    private final AccountService accountService;

    @Autowired
    public TransactionService(TransactionRepository repository, AccountService accountService) {
        this.repository = repository;
        this.accountService = accountService;
    }

    public Transaction saveTransaction(TransactionRequest request, TransactionStatus transationStatus) {
        return saveTransaction(request, transationStatus, "Transaction approved");
    }

    public Transaction saveTransaction(TransactionRequest request, TransactionStatus transactionStatus, String message) {
        if(request.type().equals(TransactionType.WITHDRAWAL)){
            Account account = accountService.findById(request.fromAccountId());
            Transaction transaction = Transaction.builder()
                    .account(account)
                    .date(request.date())
                    .amount(request.amount())
                    .type(request.type())
                    .status(transactionStatus)
                    .from(request.fromUsername())
                    .balance(account.getBalance())
                    .message(message)
                    .build();
            repository.save(transaction);
            return transaction;
        } else if (request.type().equals(TransactionType.DEPOSIT)) {
            Account account = accountService.findById(request.toAccountId());
            Transaction transaction = Transaction.builder()
                    .account(account)
                    .date(request.date())
                    .amount(request.amount())
                    .type(request.type())
                    .status(transactionStatus)
                    .from(request.fromUsername())
                    .balance(account.getBalance())
                    .message(message)
                    .build();
            repository.save(transaction);
            return transaction;
        }else {
            Account fromAccount = accountService.findById(request.fromAccountId());
            Transaction transaction1 = Transaction.builder()
                    .account(fromAccount)
                    .date(request.date())
                    .amount(request.amount())
                    .type(TransactionType.WITHDRAWAL)
                    .status(transactionStatus)
                    .from(request.fromUsername())
                    .balance(fromAccount.getBalance())
                    .message(message)
                    .build();
            repository.save(transaction1);
            Account toAccount = accountService.findById(request.toAccountId());
            Transaction transaction2 = Transaction.builder()
                    .account(toAccount)
                    .date(request.date())
                    .amount(request.amount())
                    .type(TransactionType.WITHDRAWAL)
                    .status(transactionStatus)
                    .from(request.fromUsername())
                    .balance(toAccount.getBalance())
                    .message(message)
                    .build();
            repository.save(transaction2);
            return transaction1;
        }
    }

    public List<Transaction> findAllByUserId(CustomUserDetails userDetails) {
        List<Transaction> transactions = new ArrayList<>();
        List<Account> accounts = accountService.getAccountsByUserId(userDetails);
        for (Account account: accounts) {
            transactions.addAll(repository.findAllByAccountId(account.getId()));
        }
        return transactions;
    }

}
