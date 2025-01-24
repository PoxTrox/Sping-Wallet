package org.example.spingwallet.transaction.repository;

import org.example.spingwallet.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {


    List<Transaction> findAllByOwnerIdOrderByCreatedOnDesc(UUID accountId);
}
