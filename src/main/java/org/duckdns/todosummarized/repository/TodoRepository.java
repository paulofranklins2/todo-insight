package org.duckdns.todosummarized.repository;

import org.duckdns.todosummarized.domains.entity.Todo;
import org.duckdns.todosummarized.domains.enums.TaskPriority;
import org.duckdns.todosummarized.domains.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {

    List<Todo> findByStatus(TaskStatus status);

    List<Todo> findByPriority(TaskPriority priority);

    List<Todo> findByDueDateBefore(LocalDateTime dateTime);

    List<Todo> findByDueDateAfter(LocalDateTime dateTime);
}
