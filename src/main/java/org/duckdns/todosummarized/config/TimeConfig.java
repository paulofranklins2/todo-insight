package org.duckdns.todosummarized.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig {

    /**
     * Provides a centralized Clock bean for the application.
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
