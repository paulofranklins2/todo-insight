package org.duckdns.todosummarized.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.duckdns.todosummarized.dto.DailySummaryDTO;
import org.duckdns.todosummarized.service.SummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for daily summary operations.
 */
@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
@Tag(name = "Summary", description = "Daily summary and metrics API")
public class SummaryController {

    private final SummaryService summaryService;

    /**
     * Get the daily summary with deterministic metrics.
     */
    @Operation(
            summary = "Get daily summary",
            description = "Returns deterministic metrics summarizing todos for the current day, " +
                    "including counts by status, priority, overdue items, and completion rate."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Summary retrieved successfully",
            content = @Content(schema = @Schema(implementation = DailySummaryDTO.class))
    )
    @GetMapping("/daily")
    public ResponseEntity<DailySummaryDTO> getDailySummary() {
        DailySummaryDTO summary = summaryService.getDailySummary();
        return ResponseEntity.ok(summary);
    }
}

