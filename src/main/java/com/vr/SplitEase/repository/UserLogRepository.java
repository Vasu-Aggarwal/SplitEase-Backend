package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.UserLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLogRepository extends JpaRepository<UserLogs, Integer> {
    Optional<List<UserLogs>> findByUserUuid(String userUuid);
}
