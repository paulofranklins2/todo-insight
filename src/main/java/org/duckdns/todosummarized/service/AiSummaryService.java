package org.duckdns.todosummarized.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.todosummarized.domains.entity.User;
import org.duckdns.todosummarized.domains.enums.AiProvider;
import org.duckdns.todosummarized.domains.enums.SummaryType;
import org.duckdns.todosummarized.dto.AiSummaryDTO;
import org.duckdns.todosummarized.dto.DailySummaryDTO;
import org.duckdns.todosummarized.dto.SummaryTypeDTO;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Orchestration service for AI-powered summaries.
 * Coordinates between caching, AI generation, and metrics retrieval.
 * Supports multiple AI providers (OpenAI, Gemini) with automatic failover.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiSummaryService {

    /**
     * Cached immutable list of summary type DTOs.
     * Created once at class load time since enum values are constant.
     */
    private static final List<SummaryTypeDTO> SUMMARY_TYPES = Arrays.stream(SummaryType.values())
            .map(SummaryTypeDTO::from)
            .toList();

    private final SummaryService summaryService;
    private final AiProviderSelector providerSelector;
    private final AiInsightCacheService cacheService;
    private final Clock clock;

    /**
     * Gets the stored AI insight for a user, if available.
     * Use this to check for existing insights before generating new ones.
     */
    public Optional<AiSummaryDTO> getCachedInsight(User user) {
        return cacheService.getCachedInsight(user);
    }

    /**
     * Generates an AI-powered summary for the authenticated user using automatic provider selection.
     * Falls back to metrics-only if all AI providers are disabled or fail.
     */
    public AiSummaryDTO getAiSummary(User user, SummaryType summaryType) {
        return getAiSummary(user, summaryType, AiProvider.AUTO);
    }

    /**
     * Gets an AI insight for a user with cache-first strategy.
     * Otherwise generates a new insight and caches it.
     */
    public AiSummaryDTO getAiSummary(User user, SummaryType summaryType, AiProvider provider) {
        // Check cache first
        Optional<AiSummaryDTO> cached = cacheService.getCachedInsight(user);

        // Return cached if it exists AND matches the requested type
        if (cached.isPresent() && cached.get().summaryType() == summaryType) {
            log.debug("Returning cached insight for user: {}, type: {}", user.getUsername(), summaryType);
            return cached.get();
        }

        // Generate new insight (different type requested or no cache)
        return generateNewInsight(user, summaryType, provider);
    }

    /**
     * Generates a new AI insight for the user, replacing any existing stored insight.
     * Use this when the user explicitly requests a new/different insight.
     */
    public AiSummaryDTO generateNewInsight(User user, SummaryType summaryType, AiProvider provider) {
        AiSummaryDTO newInsight = generateAiSummaryInternal(user, summaryType, provider);

        // Save to cache and database
        cacheService.saveInsight(user, newInsight, provider);

        log.info("New AI insight generated and stored for user: {}, type: {}", user.getUsername(), summaryType);
        return newInsight;
    }

    /**
     * Invalidates the stored AI insight for a user.
     * Call this when user's todos change significantly.
     */
    public void invalidateInsightCache(User user) {
        cacheService.invalidateCache(user);
        log.debug("AI insight invalidated for user: {}", user.getUsername());
    }

    /**
     * Internal method that performs the actual AI summary generation without caching.
     */
    private AiSummaryDTO generateAiSummaryInternal(User user, SummaryType summaryType, AiProvider provider) {
        LocalDate today = LocalDate.now(clock);
        DailySummaryDTO metrics = summaryService.getDailySummary(user);

        if (!providerSelector.isProviderAvailable(provider)) {
            log.info("AI summary unavailable for provider {}, returning metrics fallback for user: {}",
                    provider, user.getUsername());
            return AiSummaryDTO.fallback(today, summaryType, providerSelector.getAggregatedUnavailableReason(), metrics);
        }

        AiProviderSelector.AiGenerationResult result = providerSelector.generateSummary(metrics, summaryType, provider);

        if (result.success()) {
            log.info("AI summary generated successfully for user: {}, type: {}, provider: {}",
                    user.getUsername(), summaryType, result.provider());
            return AiSummaryDTO.aiGenerated(today, summaryType, result.summary(), result.model(), metrics);
        } else {
            log.warn("AI summary failed, returning metrics fallback for user: {}. Reason: {}",
                    user.getUsername(), result.failureReason());
            return AiSummaryDTO.fallback(today, summaryType, result.failureReason(), metrics);
        }
    }

    /**
     * Returns all available summary type options.
     */
    public List<SummaryTypeDTO> getAvailableSummaryTypes() {
        return SUMMARY_TYPES;
    }

    /**
     * Checks if any AI summary provider is currently available.
     */
    public boolean isAiAvailable() {
        return providerSelector.isAnyProviderAvailable();
    }

    /**
     * Gets information about available AI providers.
     */
    public AiProviderSelector.ProviderInfo[] getProviderInfo() {
        return providerSelector.getProviderInfo();
    }
}

