package com.phegondev.inventorymgtsystem.repositories;

import com.phegondev.inventorymgtsystem.models.Transaction;
import com.phegondev.inventorymgtsystem.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    List<Transaction> findByStatus(TransactionStatus status);
    java.util.Optional<Transaction> findTopByProductIdAndTransactionTypeOrderByIdDesc(Long productId, com.phegondev.inventorymgtsystem.enums.TransactionType transactionType);
}
