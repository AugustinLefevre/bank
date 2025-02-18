package com.itinov.bank.requests;

import com.itinov.bank.entities.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest(
        LocalDateTime date,
        BigDecimal amount,
        TransactionType type,

        String fromUsername,
        Long fromAccountId,
        Long toAccountId){
}
