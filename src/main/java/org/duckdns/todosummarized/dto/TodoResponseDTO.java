package org.duckdns.todosummarized.dto;

import org.duckdns.todosummarized.domains.enums.TaskPriority;
import org.duckdns.todosummarized.domains.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TodoResponseDTO(
        UUID id,
        String title,
        String description,
        TaskPriority priority,
        TaskStatus status,
        LocalDateTime dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
