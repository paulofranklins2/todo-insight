package org.duckdns.todosummarized.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for JWT authentication response containing access and refresh tokens.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response containing JWT tokens")
public class AuthTokenResponseDTO {

    @Schema(description = "JWT access token for API authorization",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "JWT refresh token for obtaining new access tokens",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Schema(description = "Token type (always 'Bearer')", example = "Bearer")
    @JsonProperty("token_type")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Access token expiration time in seconds", example = "900")
    @JsonProperty("expires_in")
    private long expiresIn;

    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "User role", example = "ROLE_USER")
    private String role;
}

