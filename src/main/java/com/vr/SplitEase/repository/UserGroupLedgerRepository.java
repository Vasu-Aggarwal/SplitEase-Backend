package com.vr.SplitEase.repository;

import com.vr.SplitEase.config.constants.GroupStatus;
import com.vr.SplitEase.entity.Group;
import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.entity.UserGroupLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserGroupLedgerRepository extends JpaRepository<UserGroupLedger, Integer> {
    Boolean existsByUserAndGroupAndStatus(User user, Group group, Integer status);
    Optional<UserGroupLedger> findByUserAndGroup(User user, Group group);
    Optional<List<UserGroupLedger>> findByGroup(Group groupId);
    Optional<List<UserGroupLedger>> findByGroupAndStatus(Group groupId, Integer status);
    Optional<List<UserGroupLedger>> findByUserAndStatus(User user, Integer status);
}
