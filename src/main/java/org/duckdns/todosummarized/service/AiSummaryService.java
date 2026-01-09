package org.duckdns.todosummarized.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.todosummarized.domains.entity.User;
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
 * Service for generating AI-powered summaries with automatic fallback to metrics-only.
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
    private final AiSummaryAdapter aiAdapter;
    private final Clock clock;

    /**
     * Generates an AI-powered summary for the authenticated user.
     * Falls back to metrics-only if AI is disabled or fails.
     *
     * @param user        the authenticated user
     * @param summaryType the type of summary to generate
     * @return AI summary with optional fallback to metrics
     */
    public AiSummaryDTO getAiSummary(User user, SummaryType summaryType) {
        LocalDate today = LocalDate.now(clock);
        DailySummaryDTO metrics = summaryService.getDailySummary(user);

        if (!aiAdapter.isEnabled()) {
            log.info("AI summary disabled, returning metrics fallback for user: {}", user.getUsername());
            return AiSummaryDTO.fallback(today, summaryType, aiAdapter.getUnavailableReason(), metrics);
        }

        Optional<String> aiSummary = aiAdapter.generateSummary(metrics, summaryType);

        if (aiSummary.isPresent()) {
            log.info("AI summary generated successfully for user: {}, type: {}", user.getUsername(), summaryType);
            return AiSummaryDTO.aiGenerated(today, summaryType, aiSummary.get(), aiAdapter.getModel(), metrics);
        } else {
            log.warn("AI summary failed, returning metrics fallback for user: {}", user.getUsername());
            return AiSummaryDTO.fallback(today, summaryType, aiAdapter.getUnavailableReason(), metrics);
        }
    }

    /**
     * Returns all available summary type options.
     *
     * @return unmodifiable list of summary type DTOs
     */
    public List<SummaryTypeDTO> getAvailableSummaryTypes() {
        return SUMMARY_TYPES;
    }

    /**
     * Checks if AI summary feature is currently available.
     *
     * @return true if AI is enabled and configured
     */
    public boolean isAiAvailable() {
        return aiAdapter.isEnabled();
    }
}

