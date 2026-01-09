package org.duckdns.todosummarized.repository.projection;

import org.duckdns.todosummarized.domains.enums.TaskStatus;

/**
 * Projection for status count aggregation query results.
 */
public interface StatusCountProjection {

    TaskStatus getStatus();

    Long getCount();
}
