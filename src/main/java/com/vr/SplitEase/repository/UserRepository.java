package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
    Optional<User> findByNameOrEmailOrMobileIgnoreCase(String userData);
}
