package com.itinov.bank.services;

import com.itinov.bank.entities.Account;
import com.itinov.bank.entities.TransactionType;
import com.itinov.bank.exceptions.ForbiddenTransactionException;
import com.itinov.bank.repositories.AccountRepository;
import com.itinov.bank.requests.TransactionRequest;
import com.itinov.bank.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public List<Account> getAccountsByUserId(CustomUserDetails userInfo){
        return accountRepository.findAllByUserId(userInfo.getUserId());
    }

    @Transactional
    public void makeTransaction(TransactionRequest request) throws ForbiddenTransactionException {
            if(request.type().equals(TransactionType.TRANSFER)){
                makeTransfer(request);
            }else if(request.type().equals(TransactionType.DEPOSIT)){
                makeDeposit(request);
            } else if (request.type().equals(TransactionType.WITHDRAWAL)) {
                makeWithdrawal(request);
            }
    }

    private void makeTransfer(TransactionRequest request) {
        BigDecimal amout = request.amount().abs();
        if(request.fromAccountId() == null) {
            throw new ForbiddenTransactionException("Sender account id is null");
        } else if (request.toAccountId() == null) {
            throw new ForbiddenTransactionException("Receiver account id is null");
        }
        Account from = accountRepository
                .findById(request.fromAccountId())
                .orElseThrow(() -> new ForbiddenTransactionException("Sender account not found"));
        if(from.getBalance().compareTo(request.amount()) < 0){
            throw new ForbiddenTransactionException("Transaction amount is greater than the account balance");
        }
        from.setBalance(from.getBalance().subtract(amout).setScale(2, BigDecimal.ROUND_HALF_UP));
        accountRepository.save(from);

        Account to = accountRepository
                .findById(request.toAccountId())
                .orElseThrow(() -> new ForbiddenTransactionException("Receiver account not found"));

        to.setBalance(to.getBalance().add(amout).setScale(2, BigDecimal.ROUND_HALF_UP));
        accountRepository.save(to);
    }



    private void makeWithdrawal(TransactionRequest request) {
        BigDecimal amout = request.amount().abs();
        if(request.fromAccountId() == null) {
            throw new ForbiddenTransactionException("Sender account id is null");
        }
        Account from = accountRepository
                .findById(request.fromAccountId())
                .orElseThrow(() -> new ForbiddenTransactionException("Sender account not found"));
        if(from.getBalance().compareTo(request.amount()) < 0){
            throw new ForbiddenTransactionException("Transaction amount is greater than the account balance");
        }
        from.setBalance(from.getBalance().subtract(amout).setScale(2, BigDecimal.ROUND_HALF_UP));
        accountRepository.save(from);
    }

    private void makeDeposit(TransactionRequest request) {
        BigDecimal amout = request.amount().abs();
        if(request.toAccountId() == null) {
            throw new ForbiddenTransactionException("Receiver account id is null");
        }
        Account to = accountRepository
                .findById(request.toAccountId())
                .orElseThrow(() -> new ForbiddenTransactionException("Sender account not found"));
        to.setBalance(to.getBalance().add(amout).setScale(2, BigDecimal.ROUND_HALF_UP));
        accountRepository.save(to);
    }

    public Account findById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new ForbiddenTransactionException("Account not found"));
    }
}
