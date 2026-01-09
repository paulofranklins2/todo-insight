package org.duckdns.todosummarized.domains.enums;

import lombok.Getter;

/**
 * Predefined summary types with persona-specific prompts for AI-generated summaries.
 * Each type is tailored for different user needs and contexts.
 */
@Getter
public enum SummaryType {

    /**
     * High-level, outcome-focused summary for executives and managers.
     */
    EXECUTIVE(
            "Executive / Manager",
            "High-level, outcome-focused, no task noise.",
            "Summarize today's todo list into a high-level progress update. Focus on outcomes, risks, and what still needs attention. Keep it concise and suitable for leadership review."
    ),

    /**
     * Structured, technical summary for software engineers and developers.
     */
    DEVELOPER(
            "Software Engineer / Developer",
            "Structured, technical, standup-ready.",
            "Convert my todo list into a daily engineering summary. Separate completed work, in-progress tasks, and carry-overs. Highlight blockers, decisions made, and next technical steps."
    ),

    /**
     * Learning-oriented summary for students.
     */
    STUDENT(
            "Student",
            "Learning-oriented, clarity-first.",
            "Summarize my daily tasks with a focus on learning progress. Identify what was completed, what needs review, and what should be prioritized tomorrow. Keep the language simple and clear."
    ),

    /**
     * Low cognitive load summary for ADHD and focus support.
     */
    FOCUS_SUPPORT(
            "Focus Support",
            "Low cognitive load, actionable.",
            "Simplify my todo list into a clear and calm daily summary. Reduce it to the most important tasks only. Suggest the next single action to start tomorrow."
    ),

    /**
     * Flow-oriented summary for creatives (designers, writers, artists).
     */
    CREATIVE(
            "Creative (Designer, Writer, Artist)",
            "Flow-oriented, non-rigid.",
            "Summarize my daily tasks in a way that reflects creative progress. Highlight what was created, what is evolving, and what ideas should be revisited. Avoid rigid structure."
    ),

    /**
     * Process-focused summary for operations, support, and logistics.
     */
    OPERATIONS(
            "Operations / Support / Logistics",
            "Process, throughput, accountability.",
            "Turn my todo list into an operational daily report. Show completed tasks, pending items, and any delays or dependencies. Keep it factual and process-focused."
    ),

    /**
     * Friendly and practical summary for personal life and home tasks.
     */
    PERSONAL(
            "Personal Life / Home Tasks",
            "Friendly but practical.",
            "Summarize my personal todo list for the day. Highlight what got done, what can wait, and the top priorities for tomorrow. Keep it short and encouraging."
    ),

    /**
     * Collaborative and transparent summary for team standups.
     */
    STANDUP(
            "Team Standup (Shared)",
            "Collaborative and transparent.",
            "Create a standup-style summary from my todo list. Include what was completed, what I'm working on, and anything blocking progress. Keep it brief and team-friendly."
    ),

    /**
     * Reflective summary for weekly reviews.
     */
    WEEKLY_REVIEW(
            "Weekly Review (Individual)",
            "Reflective but concrete.",
            "Review my todo list for the week and summarize progress. Identify patterns, recurring delays, and key accomplishments. Suggest one improvement for next week."
    ),

    /**
     * Ultra-minimal summary for dashboards and notifications.
     */
    MINIMAL(
            "Ultra-Minimal",
            "For dashboards or notifications.",
            "Summarize my todo list in under 5 bullet points. Prioritize clarity and action over detail."
    );

    private final String displayName;
    private final String description;
    private final String prompt;

    SummaryType(String displayName, String description, String prompt) {
        this.displayName = displayName;
        this.description = description;
        this.prompt = prompt;
    }
}

