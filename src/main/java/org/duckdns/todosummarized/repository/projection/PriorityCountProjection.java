package org.duckdns.todosummarized.repository.projection;

import org.duckdns.todosummarized.domains.enums.TaskPriority;

/**
 * Projection for priority count aggregation query results.
 */
public interface PriorityCountProjection {

    TaskPriority getPriority();

    Long getCount();
}