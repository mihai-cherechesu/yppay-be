package com.bank.repository;

import com.bank.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    public List<Transaction> getTransactionsBySenderId(Long id);
    public List<Transaction> getTransactionsByReceiverId(Long id);

}
