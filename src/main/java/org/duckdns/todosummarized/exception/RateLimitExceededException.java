package org.duckdns.todosummarized.exception;

import lombok.Getter;

/**
 * Exception thrown when a user exceeds the rate limit for an endpoint.
 */
@Getter
public class RateLimitExceededException extends RuntimeException {
    private final long retryAfterSeconds;

    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public RateLimitExceededException(long retryAfterSeconds) {
        this("Rate limit exceeded. Please try again later.", retryAfterSeconds);
    }

}

