package com.itinov.bank.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
public class Transaction{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    private BigDecimal amount;
    @Column(name = "transaction_date")
    private LocalDateTime date;
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    @Column(name = "transaction_status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @Column(name = "from_username")
    private String from;
    private BigDecimal balance;
    private String message;
}
