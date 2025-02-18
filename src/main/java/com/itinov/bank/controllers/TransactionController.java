package com.itinov.bank.controllers;

import com.itinov.bank.entities.Transaction;
import com.itinov.bank.entities.TransactionStatus;
import com.itinov.bank.exceptions.ForbiddenTransactionException;
import com.itinov.bank.requests.TransactionRequest;
import com.itinov.bank.security.CustomUserDetails;
import com.itinov.bank.services.AccountService;
import com.itinov.bank.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;

    @PostMapping("/transaction")
    public ResponseEntity makeTransaction(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody TransactionRequest request){
        if(userDetails ==null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated user");
        }
        try{
            accountService.makeTransaction(request);
            Transaction transaction = transactionService.saveTransaction(request, TransactionStatus.APPROVED);
            return ResponseEntity.ok(transaction);
        }catch (ForbiddenTransactionException e){
            Transaction transaction = transactionService.saveTransaction(request, TransactionStatus.DENIED, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(transaction);
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity getTransactions(@AuthenticationPrincipal CustomUserDetails userDetails){
        if(userDetails ==null)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated user");
        }
        List<Transaction> transactions = transactionService.findAllByUserId(userDetails);
        return ResponseEntity.ok(transactions);
    }
}
