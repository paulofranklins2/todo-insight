package org.duckdns.todosummarized.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Map;

/**
 * Response DTO containing daily summary metrics for todos.
 */
@Builder
@Schema(description = "Daily summary of todo metrics")
public record DailySummaryDTO(
        @Schema(description = "Date of the summary", example = "2026-01-09")
        LocalDate date,

        @Schema(description = "Total number of todos", example = "25")
        long totalTodos,

        @Schema(description = "Number of completed todos", example = "10")
        long completedCount,

        @Schema(description = "Number of todos in progress", example = "8")
        long inProgressCount,

        @Schema(description = "Number of todos not started", example = "5")
        long notStartedCount,

        @Schema(description = "Number of cancelled todos", example = "2")
        long cancelledCount,

        @Schema(description = "Number of overdue todos", example = "3")
        long overdueCount,

        @Schema(description = "Number of todos due today", example = "4")
        long dueTodayCount,

        @Schema(description = "Number of upcoming todos (due within 7 days)", example = "6")
        long upcomingCount,

        @Schema(description = "Completion rate as percentage (0-100)", example = "40.0")
        double completionRate,

        @Schema(description = "Breakdown of todos by priority")
        Map<String, Long> byPriority,

        @Schema(description = "Breakdown of todos by status")
        Map<String, Long> byStatus
) {
}

