package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userRepository.findByEmail(email)
                    .or(() -> {
                        throw new ResourceNotFoundException("User not found");
                    });
        } else {
            return Optional.empty();
        }
    }

    public Optional<String> getCurrentUserUuid() {
        return getCurrentUser().map(User::getUserUuid);
    }

    public Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(User::getEmail);
    }
}
