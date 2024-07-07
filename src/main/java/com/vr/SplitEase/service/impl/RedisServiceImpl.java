package com.vr.SplitEase.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vr.SplitEase.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public <T> T get(String key, Class<T> entityClass) {
        try{
            String json = (String) redisTemplate.opsForValue().get(key);
            return objectMapper.readValue(json, entityClass);
        } catch (Exception e){
            log.error("Error while getting data from Redis" +e.getMessage());
            return null;
        }
    }

    @Override
    public <T> List<T> getList(String key, TypeReference<List<T>> typeReference) {
        try {
            String json = (String) redisTemplate.opsForValue().get(key);
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("Error while getting data from Redis: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void set(String key, Object object, Long ttl) {
        try{
            String json = objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(key, json, ttl, TimeUnit.SECONDS);
        } catch (Exception e){
            log.error("Error while setting data in Redis" +e.getMessage());
        }
    }
}
