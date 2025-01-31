package org.example.spingwallet.transaction.service;


import lombok.extern.slf4j.Slf4j;
import org.example.spingwallet.exception.DomainException;
import org.example.spingwallet.transaction.model.Transaction;
import org.example.spingwallet.transaction.model.TransactionStatus;
import org.example.spingwallet.transaction.model.TransactionType;
import org.example.spingwallet.transaction.repository.TransactionRepository;
import org.example.spingwallet.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class TransactionService {


    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public Transaction createNewTransaction(User owner, String Sender, String receiver, BigDecimal amount, BigDecimal balanceLeft, Currency currency, TransactionType transactionType, TransactionStatus transactionStatus, String transactionDescription, String failureReason) {

        LocalDateTime now = LocalDateTime.now();

        Transaction transaction = Transaction.builder()
                .owner(owner)
                .sender(Sender)
                .receiver(receiver)
                .amount(amount)
                .balanceLeft(balanceLeft)
                .currency(currency)
                .type(transactionType)
                .status(transactionStatus)
                .description(transactionDescription)
                .failureReason(failureReason)
                .createdOn(now)
                .build();
        return transactionRepository.save(transaction);
    }

    public List<Transaction>  getAllTransaction(UUID ownerId) {

        return transactionRepository.findAllByOwnerIdOrderByCreatedOnDesc(ownerId);
    }

    public Transaction getTransactionById(UUID id) {
        Optional<Transaction> byId = transactionRepository.findById(id);

        return  byId.orElseThrow(()-> new DomainException("Transaction with id -> %s does not exist ".formatted(id)));
    }
}
