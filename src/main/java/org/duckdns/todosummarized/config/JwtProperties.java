package org.duckdns.todosummarized.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for JWT authentication.
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret key for signing JWT tokens.
     */
    private String secretKey;

    /**
     * Access token expiration time in milliseconds.
     */
    private long accessTokenExpiration;

    /**
     * Refresh token expiration time in milliseconds.
     */
    private long refreshTokenExpiration;

    /**
     * Token issuer identifier.
     */
    private String issuer;
}
