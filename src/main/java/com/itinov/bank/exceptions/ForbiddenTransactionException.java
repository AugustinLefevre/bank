package com.itinov.bank.exceptions;

public class ForbiddenTransactionException extends RuntimeException{
    public ForbiddenTransactionException(String message) {
        super(message);
    }
}
