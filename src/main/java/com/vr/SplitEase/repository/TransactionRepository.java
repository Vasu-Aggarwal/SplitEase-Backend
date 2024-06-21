package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.Group;
import com.vr.SplitEase.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Procedure(procedureName = "CalculateNetBalance")
    void calculateNetBalance(@Param("groupId") Integer groupId);

    Optional<List<Transaction>> findByGroup(Group group);
}
