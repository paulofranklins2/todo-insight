package org.duckdns.todosummarized.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for OpenAI API integration.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {

    /**
     * OpenAI API key for authentication.
     */
    private String apiKey;

    /**
     * OpenAI model to use.
     */
    private String model = "gpt-4.1-nano";

    /**
     * Whether AI summary feature is enabled.
     */
    private boolean enabled = true;

    /**
     * Maximum tokens for AI response.
     */
    private int maxTokens = 500;

    /**
     * Temperature for AI response (0.0 - 2.0).
     * Higher values make output more random.
     */
    private double temperature = 0.7;

    /**
     * Request timeout in seconds.
     */
    private int timeoutSeconds = 30;
}

