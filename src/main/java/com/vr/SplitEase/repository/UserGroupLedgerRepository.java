package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.Group;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.entity.UserGroupLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserGroupLedgerRepository extends JpaRepository<UserGroupLedger, Integer> {
    Boolean existsByUserAndGroup(User user, Group group);
    Optional<UserGroupLedger> findByUserAndGroup(User user, Group group);
    Optional<List<UserGroupLedger>> findByGroup(Group groupId);
}
