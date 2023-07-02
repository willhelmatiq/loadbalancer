package com.example.hs.loadbalancer.service;

import com.example.hs.loadbalancer.model.LimiterMode;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BucketService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {
        LimiterMode limiterMode = LimiterMode.resolvePlanFromApiKey(apiKey);
        return Bucket.builder()
                .addLimit(limiterMode.getLimit())
                .build();
    }
}
