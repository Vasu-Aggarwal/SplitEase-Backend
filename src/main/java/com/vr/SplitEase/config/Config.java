package com.vr.SplitEase.config;

import com.vr.SplitEase.service.impl.AuditorAwareImpl;
import com.vr.SplitEase.service.impl.CurrentUserService;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

@Configuration
@EnableJpaAuditing
public class Config {

    private final CurrentUserService currentUserService;

    public Config(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAwareImpl(currentUserService);
    }

    @PostConstruct
    public void init() {

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+5:30"));
    }
}
