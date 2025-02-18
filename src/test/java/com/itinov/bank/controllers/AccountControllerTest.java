package com.itinov.bank.controllers;

import com.itinov.bank.entities.*;
import com.itinov.bank.security.CustomUserDetails;
import com.itinov.bank.services.TransactionService;
import jakarta.transaction.Transactional;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@SpringBootTest
@Transactional
public class AccountControllerTest {
    @Autowired
    private AccountController accountController;

    private final CustomUserDetails customUserDetails;
    private final User user;

    @Autowired
    private TransactionService transactionService;

    public AccountControllerTest() {

        user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setPassword("hashed_password");

        this.customUserDetails = new CustomUserDetails(user);
    }

    @Test
    void testGetUserAccounts() {
        ResponseEntity<List<Account>> actual = accountController.getUserAccounts(customUserDetails);
        Assert.assertEquals(HttpStatus.OK, actual.getStatusCode());
        Assert.assertTrue(actual.getBody()
                .size() == 2);
    }

}