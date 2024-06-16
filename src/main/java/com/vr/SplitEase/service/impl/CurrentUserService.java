package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.exception.BadApiRequestException;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
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
            log.debug("Fetching user with email: {}", email);
            try {
                return Optional.ofNullable(userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found")));
            } catch (Exception e){
                log.error("This is throwing stack overflow error");
                throw new BadApiRequestException(e.getMessage());
            }
        } else {
            return Optional.empty();
        }
    }

    public Optional<String> getCurrentUserUuid() {
        log.debug("Fetching current user UUID");
        return getCurrentUser().map(User::getUserUuid);
    }

    public Optional<String> getCurrentUserEmail() {
        log.debug("Fetching current user email");
        return getCurrentUser().map(User::getEmail);
    }
}
