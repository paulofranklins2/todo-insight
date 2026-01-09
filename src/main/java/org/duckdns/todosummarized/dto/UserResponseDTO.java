package org.duckdns.todosummarized.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.duckdns.todosummarized.domains.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for user responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private String email;
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert a User entity to UserResponseDTO.
     *
     * @param user the user entity
     * @return the response DTO
     */
    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
