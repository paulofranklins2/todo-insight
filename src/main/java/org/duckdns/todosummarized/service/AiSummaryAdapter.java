package org.duckdns.todosummarized.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.todosummarized.config.OpenAiProperties;
import org.duckdns.todosummarized.domains.enums.SummaryType;
import org.duckdns.todosummarized.dto.DailySummaryDTO;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Adapter service for AI-powered summary generation using OpenAI API.
 * Handles API communication, error handling, and fallback scenarios.
 * Uses a shared HttpClient instance for connection pooling and performance.
 */
@Slf4j
@Service
public class AiSummaryAdapter {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String AUTH_HEADER_PREFIX = "Bearer ";

    private final OpenAiProperties openAiProperties;
    private final ObjectMapper objectMapper;
    private HttpClient httpClient;

    public AiSummaryAdapter(OpenAiProperties openAiProperties, ObjectMapper objectMapper) {
        this.openAiProperties = openAiProperties;
        this.objectMapper = objectMapper;
    }

    /**
     * Initializes the shared HttpClient after dependency injection.
     */
    @PostConstruct
    void initHttpClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(openAiProperties.getTimeoutSeconds()))
                .build();
        log.info("AI Summary adapter initialized with model: {}, enabled: {}",
                openAiProperties.getModel(), openAiProperties.isEnabled());
    }

    /**
     * Closes the HttpClient when the bean is destroyed.
     */
    @PreDestroy
    void destroyHttpClient() {
        if (httpClient != null) {
            httpClient.close();
            log.info("AI Summary adapter HttpClient closed");
        }
    }

    /**
     * Checks if AI summary feature is enabled.
     */
    public boolean isEnabled() {
        return openAiProperties.isEnabled();
    }

    /**
     * Gets the configured model name.
     */
    public String getModel() {
        return openAiProperties.getModel();
    }

    /**
     * Generates an AI summary for the given metrics using the specified summary type.
     * Returns empty Optional if AI is disabled or an error occurs.
     *
     * @param metrics     the daily summary metrics to summarize
     * @param summaryType the type of summary to generate
     * @return Optional containing the generated summary, or empty if unavailable
     */
    public Optional<String> generateSummary(DailySummaryDTO metrics, SummaryType summaryType) {
        if (!openAiProperties.isEnabled()) {
            log.info("AI summary is disabled by configuration");
            return Optional.empty();
        }

        if (openAiProperties.getApiKey() == null || openAiProperties.getApiKey().isBlank()) {
            log.warn("OpenAI API key is not configured");
            return Optional.empty();
        }

        try {
            String userMessage = buildUserMessage(metrics);
            String response = callOpenAi(summaryType.getPrompt(), userMessage);
            return Optional.ofNullable(response);
        } catch (Exception e) {
            log.error("Failed to generate AI summary: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Builds the user message containing todo metrics for the AI.
     */
    private String buildUserMessage(DailySummaryDTO metrics) {
        StringBuilder sb = new StringBuilder();
        sb.append("Here are my todo metrics for today (").append(metrics.date()).append("):\n\n");
        sb.append("Total todos: ").append(metrics.totalTodos()).append("\n");
        sb.append("Completed: ").append(metrics.completedCount()).append("\n");
        sb.append("In Progress: ").append(metrics.inProgressCount()).append("\n");
        sb.append("Not Started: ").append(metrics.notStartedCount()).append("\n");
        sb.append("Cancelled: ").append(metrics.cancelledCount()).append("\n");
        sb.append("Overdue: ").append(metrics.overdueCount()).append("\n");
        sb.append("Due Today: ").append(metrics.dueTodayCount()).append("\n");
        sb.append("Upcoming (next 7 days): ").append(metrics.upcomingCount()).append("\n");
        sb.append("Completion Rate: ").append(metrics.completionRate()).append("%\n\n");

        sb.append("By Priority:\n");
        metrics.byPriority().forEach((priority, count) ->
                sb.append("  - ").append(priority).append(": ").append(count).append("\n"));

        sb.append("\nBy Status:\n");
        metrics.byStatus().forEach((status, count) ->
                sb.append("  - ").append(status).append(": ").append(count).append("\n"));

        return sb.toString();
    }

    /**
     * Calls the OpenAI API with the given system prompt and user message.
     *
     * @param systemPrompt the system prompt for context
     * @param userMessage  the user message with metrics
     * @return the AI-generated response content
     * @throws Exception if API call fails
     */
    private String callOpenAi(String systemPrompt, String userMessage) throws Exception {
        Duration requestTimeout = Duration.ofSeconds(openAiProperties.getTimeoutSeconds());

        Map<String, Object> requestBody = Map.of(
                "model", openAiProperties.getModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "max_tokens", openAiProperties.getMaxTokens(),
                "temperature", openAiProperties.getTemperature()
        );

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_URL))
                .header("Content-Type", CONTENT_TYPE_JSON)
                .header("Authorization", AUTH_HEADER_PREFIX + openAiProperties.getApiKey())
                .timeout(requestTimeout)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("OpenAI API error: status={}, body={}", response.statusCode(), response.body());
            throw new RuntimeException("OpenAI API returned status " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode choices = root.get("choices");
        if (choices != null && choices.isArray() && !choices.isEmpty()) {
            JsonNode message = choices.get(0).get("message");
            if (message != null && message.has("content")) {
                return message.get("content").asText();
            }
        }

        throw new RuntimeException("Unexpected OpenAI response format");
    }

    /**
     * Returns the reason why AI is unavailable.
     *
     * @return description of why AI cannot be used
     */
    public String getUnavailableReason() {
        if (!openAiProperties.isEnabled()) {
            return "AI summary feature is disabled";
        }
        if (openAiProperties.getApiKey() == null || openAiProperties.getApiKey().isBlank()) {
            return "OpenAI API key is not configured";
        }
        return "AI service encountered an error";
    }
}

