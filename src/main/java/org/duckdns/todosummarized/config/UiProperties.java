package org.duckdns.todosummarized.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * UI configuration properties.
 * Centralizes all UI-related configuration to avoid hardcoded values.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.ui")
public class UiProperties {

    /**
     * Application name displayed in UI.
     */
    private String appName;

    /**
     * Application version.
     */
    private String version;

    /**
     * Base API path.
     */
    private String apiBase;

    /**
     * Default date format for display.
     */
    private String dateFormat;

    /**
     * Default date-time format for display.
     */
    private String dateTimeFormat;
}

