package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.UserLogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLogRepository extends JpaRepository<UserLogs, Integer> {
}
