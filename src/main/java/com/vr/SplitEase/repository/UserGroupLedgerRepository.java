package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.Group;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.entity.UserGroupLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupLedgerRepository extends JpaRepository<UserGroupLedger, Integer> {
    Boolean existsByUserAndGroup(User user, Group group);
}
