package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
