package org.duckdns.todosummarized.exception;

import java.util.UUID;

/**
 * Exception thrown when a todo is not found.
 */
public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(UUID id) {
        super("Todo not found with id: " + id);
    }
}
