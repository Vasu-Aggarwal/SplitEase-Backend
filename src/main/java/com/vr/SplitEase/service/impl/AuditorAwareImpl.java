package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            User user = userRepository.findByEmail(((UserDetails) principal).getUsername()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return Optional.of(user.getUserUuid());
        } else {
            return Optional.of(principal.toString());
        }
    }
}
