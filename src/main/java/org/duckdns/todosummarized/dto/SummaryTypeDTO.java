package org.duckdns.todosummarized.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.duckdns.todosummarized.domains.enums.SummaryType;

/**
 * DTO describing a summary type option for the user.
 */
@Schema(description = "Available summary type option")
public record SummaryTypeDTO(

        @Schema(description = "Enum value for API calls", example = "DEVELOPER")
        String value,

        @Schema(description = "Human-readable name", example = "Software Engineer / Developer")
        String displayName,

        @Schema(description = "Short description", example = "Structured, technical, standup-ready.")
        String description
) {

    /**
     * Creates a DTO from a SummaryType enum value.
     */
    public static SummaryTypeDTO from(SummaryType type) {
        return new SummaryTypeDTO(
                type.name(),
                type.getDisplayName(),
                type.getDescription()
        );
    }
}

