package com.yigidolar.apiproject.Service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 5;
    private final long LOCK_TIME_DURATION = 15 * 60 * 1000; // 15 dakika

    private ConcurrentHashMap<String, Integer> attemptCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> lockTimeCache = new ConcurrentHashMap<>();

    public void loginFailed(String key) {
        int attempts = attemptCache.getOrDefault(key, 0);
        attempts++;
        attemptCache.put(key, attempts);

        if (attempts >= MAX_ATTEMPT) {
            lockTimeCache.put(key, System.currentTimeMillis());
        }
    }

    public void loginSucceeded(String key) {
        attemptCache.remove(key);
        lockTimeCache.remove(key);
    }

    public boolean isBlocked(String key) {
        if (!lockTimeCache.containsKey(key)) {
            return false;
        }

        long lockTime = lockTimeCache.get(key);
        if ((System.currentTimeMillis() - lockTime) > LOCK_TIME_DURATION) {
            lockTimeCache.remove(key);
            attemptCache.remove(key);
            return false;
        }

        return true;
    }
}

