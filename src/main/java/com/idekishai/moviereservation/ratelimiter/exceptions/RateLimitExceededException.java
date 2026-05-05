package com.idekishai.moviereservation.ratelimiter.exceptions;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException() {
        super("Rate limit exceeded, slow down");
    }
}
