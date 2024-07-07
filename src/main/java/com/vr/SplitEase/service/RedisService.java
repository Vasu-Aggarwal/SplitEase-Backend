package com.vr.SplitEase.service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public interface RedisService {
    <T> T get(String key, Class<T> entityClass);
    void set(String key, Object object, Long ttl);
    <T> List<T> getList(String key, TypeReference<List<T>> typeReference);
}
