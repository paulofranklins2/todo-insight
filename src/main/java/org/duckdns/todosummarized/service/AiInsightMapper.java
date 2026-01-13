package org.duckdns.todosummarized.service;

import org.duckdns.todosummarized.domains.entity.AiInsight;
import org.duckdns.todosummarized.domains.entity.User;
import org.duckdns.todosummarized.domains.enums.AiProvider;
import org.duckdns.todosummarized.dto.AiSummaryDTO;
import org.duckdns.todosummarized.dto.DailySummaryDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between AiInsight entity and AiSummaryDTO.
 */
@Component
public class AiInsightMapper {

    /**
     * Converts an AiInsight entity to AiSummaryDTO.
     */
    public AiSummaryDTO toDTO(AiInsight entity, DailySummaryDTO metrics) {
        if (entity == null) {
            return null;
        }

        if (entity.isAiGenerated()) {
            return AiSummaryDTO.aiGenerated(
                    entity.getSummaryDate(),
                    entity.getSummaryType(),
                    entity.getSummary(),
                    entity.getModel(),
                    metrics
            );
        }

        return AiSummaryDTO.fallback(
                entity.getSummaryDate(),
                entity.getSummaryType(),
                entity.getFallbackReason(),
                metrics
        );
    }

    /**
     * Creates a new AiInsight entity for a user from a DTO.
     */
    public AiInsight toEntity(User user, AiSummaryDTO dto, AiProvider provider) {
        if (user == null || dto == null) {
            return null;
        }

        AiInsight entity = AiInsight.builder()
                .user(user)
                .build();

        updateEntity(entity, dto, provider);
        return entity;
    }

    /**
     * Updates an AiInsight entity from an AiSummaryDTO.
     */
    public void updateEntity(AiInsight entity, AiSummaryDTO dto, AiProvider provider) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setSummaryDate(dto.date());
        entity.setSummaryType(dto.summaryType());
        entity.setProvider(provider);

        entity.setAiGenerated(dto.aiGenerated());
        entity.setSummary(dto.summary());
        entity.setModel(dto.model());
        entity.setFallbackReason(dto.fallbackReason());
    }
}