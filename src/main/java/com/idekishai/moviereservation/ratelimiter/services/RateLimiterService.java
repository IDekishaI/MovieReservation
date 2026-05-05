package com.idekishai.moviereservation.ratelimiter.services;

import com.idekishai.moviereservation.ratelimiter.exceptions.RateLimitExceededException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RateLimiterService {
    private final LettuceBasedProxyManager<String> proxyManager;

    public void checkLimit(String key, Supplier<BucketConfiguration> configSupplier) {
        Bucket bucket = proxyManager.builder().build(key, configSupplier);
        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException();
        }
    }
}