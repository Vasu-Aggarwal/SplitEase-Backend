package com.vr.SplitEase.repository;

import com.vr.SplitEase.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :userData, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :userData, '%')) " +
            "OR LOWER(u.mobile) LIKE LOWER(CONCAT('%', :userData, '%'))")
    Optional<List<User>> findByNameOrEmailOrMobileIgnoreCase(@Param("userData") String userData);
}
