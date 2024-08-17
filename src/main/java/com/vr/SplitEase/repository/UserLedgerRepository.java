package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.Transaction;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.entity.UserLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserLedgerRepository extends JpaRepository<UserLedger, Integer> {
    Optional<List<UserLedger>> findByTransaction(Transaction transactionId);
    Optional<UserLedger> findByTransactionAndUser(Transaction transaction, User user);

    @Query("SELECT transaction FROM UserLedger ul WHERE ul.user = :userUuid and ul.isActive = 'ACTIVE' ORDER BY ul.ledgerId DESC")
    Optional<List<Transaction>> findTransactionsByUser(User userUuid);
}
