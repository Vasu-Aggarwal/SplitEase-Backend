package com.vr.SplitEase.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAIConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenAIConfig.class);
    @Value("${openai.api.key}")
    private String apiKey;

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(((request, body, execution) -> {
            log.debug("API CHAT KEY: {}", apiKey);
            request.getHeaders().add("Authorization", "Bearer "+apiKey);
            return execution.execute(request, body);
        }));
        return  restTemplate;
    }

}
