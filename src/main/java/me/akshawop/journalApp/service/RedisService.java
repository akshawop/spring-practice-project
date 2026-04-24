package me.akshawop.journalApp.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redis;

    @Autowired
    private ObjectMapper objectMapper;

    public <T> T get(@NonNull String key, Class<T> entity) {
        try {
            Object obj = redis.opsForValue().get(key);
            if (obj == null) {
                return null;
            }
            // If RedisTemplate is configured with proper serializer, cast directly
            if (entity.isInstance(obj)) {
                return entity.cast(obj);
            }
            // Fallback: try to deserialize using ObjectMapper
            return objectMapper.readValue(obj.toString(), entity);
        } catch (Exception e) {
            return null;
        }
    }

    public void set(@NonNull String key, @NonNull Object obj, Long ttl) {
        try {
            String value = objectMapper.writeValueAsString(obj);
            if (value == null) {
                return;
            }
            redis.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Exception occurred while setting a value in redis: " + e);
        }
    }
}
