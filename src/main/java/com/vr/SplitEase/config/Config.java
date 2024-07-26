package com.vr.SplitEase.config;

import com.cloudinary.Cloudinary;
import com.vr.SplitEase.service.impl.AuditorAwareImpl;
import com.vr.SplitEase.service.impl.CurrentUserService;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
@EnableJpaAuditing
public class Config {

//    private final CurrentUserService currentUserService;
//
//    public Config(CurrentUserService currentUserService) {
//        this.currentUserService = currentUserService;
//    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public AuditorAware<String> auditorAware(){
        return new AuditorAwareImpl();
    }

    @PostConstruct
    public void init() {

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+5:30"));
    }

    @Bean
    public Cloudinary cloudinary(){
        Map cloudinaryConfig = new HashMap();
        cloudinaryConfig.put("cloud_name", "dyhym5vjz");
        cloudinaryConfig.put("api_key", "516617476357393");
        cloudinaryConfig.put("api_secret", "P7wSXRtqeiycCLLdAmDSIJBzXHo");

        return new Cloudinary(cloudinaryConfig);
    }
}
