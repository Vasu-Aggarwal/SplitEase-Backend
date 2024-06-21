package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.Transaction;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.entity.UserLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLedgerRepository extends JpaRepository<UserLedger, Integer> {
    Optional<List<UserLedger>> findByTransaction(Transaction transactionId);
    Optional<UserLedger> findByTransactionAndUser(Transaction transaction, User user);
}
