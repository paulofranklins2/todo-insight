package org.duckdns.todosummarized.service;

import org.duckdns.todosummarized.domains.entity.User;
import org.springframework.stereotype.Component;

/**
 * Utility bean for building consistent cache keys across the application.
 * Centralizes cache key generation to ensure consistency and easy maintenance.
 */
@Component
public class CacheKeyBuilder {

    private static final String AI_INSIGHT_PREFIX = "ai-insight:";

    /**
     * Builds a cache key for a user's AI insight.
     */
    public String forAiInsight(User user) {
        return AI_INSIGHT_PREFIX + user.getId().toString();
    }
}

