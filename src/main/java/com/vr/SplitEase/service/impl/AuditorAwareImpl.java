package com.vr.SplitEase.service.impl;

import com.vr.SplitEase.entity.User;
import com.vr.SplitEase.exception.ResourceNotFoundException;
import com.vr.SplitEase.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    private final CurrentUserService currentUserService;

    public AuditorAwareImpl(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return currentUserService.getCurrentUserUuid();
    }
}
