package org.duckdns.todosummarized.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.todosummarized.domains.entity.AiInsight;
import org.duckdns.todosummarized.domains.entity.User;
import org.duckdns.todosummarized.domains.enums.AiProvider;
import org.duckdns.todosummarized.dto.AiSummaryDTO;
import org.duckdns.todosummarized.dto.DailySummaryDTO;
import org.duckdns.todosummarized.repository.AiInsightRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service responsible for AI insight persistence and caching operations.
 * Manages both in-memory cache and database storage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiInsightCacheService {

    private final AiInsightRepository aiInsightRepository;
    private final AiInsightMapper aiInsightMapper;
    private final CacheKeyBuilder cacheKeyBuilder;
    private final SummaryService summaryService;
    private final Cache<String, AiSummaryDTO> aiInsightCache;

    /**
     * Gets the cached AI insight for a user, checking in-memory cache first, then database.
     */
    @Transactional(readOnly = true)
    public Optional<AiSummaryDTO> getCachedInsight(User user) {
        String cacheKey = cacheKeyBuilder.forAiInsight(user);

        // Check in-memory cache first
        AiSummaryDTO cached = aiInsightCache.getIfPresent(cacheKey);
        if (cached != null) {
            log.debug("Cache hit for AI insight, user: {}", user.getUsername());
            return Optional.of(cached);
        }

        // Fall back to database
        Optional<AiInsight> dbInsight = aiInsightRepository.findByUser(user);
        if (dbInsight.isPresent()) {
            log.debug("Database hit for AI insight, user: {}", user.getUsername());
            DailySummaryDTO metrics = summaryService.getDailySummary(user);
            AiSummaryDTO dto = aiInsightMapper.toDTO(dbInsight.get(), metrics);
            // Populate the cache for future requests
            aiInsightCache.put(cacheKey, dto);
            return Optional.of(dto);
        }

        log.debug("No stored AI insight found for user: {}", user.getUsername());
        return Optional.empty();
    }

    /**
     * Saves an AI insight to both database and in-memory cache.
     * Replaces any existing insight for the user.
     */
    @Transactional
    public void saveInsight(User user, AiSummaryDTO insight, AiProvider provider) {
        // Save to database (replace existing if any)
        AiInsight entity = aiInsightRepository.findByUser(user)
                .orElseGet(() -> AiInsight.builder().user(user).build());

        aiInsightMapper.updateEntity(entity, insight, provider);
        aiInsightRepository.save(entity);

        // Update in-memory cache
        String cacheKey = cacheKeyBuilder.forAiInsight(user);
        aiInsightCache.put(cacheKey, insight);

        log.debug("AI insight saved for user: {}", user.getUsername());
    }

    /**
     * Invalidates the stored AI insight for a user.
     * Removes from both the database and in-memory cache.
     */
    @Transactional
    public void invalidateCache(User user) {
        // Remove from database
        aiInsightRepository.findByUser(user)
                .ifPresent(insight -> aiInsightRepository.deleteByIdAndUser(insight.getId(), user));

        // Remove from in-memory cache
        String cacheKey = cacheKeyBuilder.forAiInsight(user);
        aiInsightCache.invalidate(cacheKey);

        log.debug("AI insight invalidated for user: {}", user.getUsername());
    }
}

