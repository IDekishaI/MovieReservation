package com.idekishai.moviereservation.ratelimiter.aspects;

import com.idekishai.moviereservation.ratelimiter.annotations.RateLimit;
import com.idekishai.moviereservation.ratelimiter.services.RateLimiterService;
import io.github.bucket4j.BucketConfiguration;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {
    private final RateLimiterService rateLimiterService;
    private final HttpServletRequest request;

    @Before("@annotation(rateLimit)")
    public void checkRateLimit(RateLimit rateLimit) {
        String ip = getClientIp();
        String key = "rate_limit:ip:" + ip;

        rateLimiterService.checkLimit(key, () ->
                BucketConfiguration.builder()
                        .addLimit(limit -> limit
                                .capacity(rateLimit.maxRequests())
                                .refillGreedy(rateLimit.maxRequests(), Duration.ofSeconds(rateLimit.windowSeconds())))
                        .build()
        );
    }

    private String getClientIp() {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
