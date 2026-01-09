package org.duckdns.todosummarized.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for rate limiting.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitProperties {

    /**
     * Whether rate limiting is enabled.
     */
    private boolean enabled = true;

    /**
     * Rate limit configuration for the AI summary endpoint.
     */
    private EndpointLimit aiSummary = new EndpointLimit();

    /**
     * Rate limit configuration for the daily summary endpoint.
     */
    private EndpointLimit dailySummary = new EndpointLimit();

    /**
     * Configuration for a specific endpoint's rate limit.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndpointLimit {
        /**
         * Maximum number of requests allowed in the time window.
         */
        private int maxRequests;

        /**
         * Time window in seconds.
         */
        private int windowSeconds;
    }
}
