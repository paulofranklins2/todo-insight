package org.duckdns.todosummarized.repository;

import org.duckdns.todosummarized.domains.entity.Todo;
import org.duckdns.todosummarized.domains.enums.TaskStatus;
import org.duckdns.todosummarized.repository.projection.PriorityCountProjection;
import org.duckdns.todosummarized.repository.projection.StatusCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Todo entity.
 */
public interface TodoRepository extends JpaRepository<Todo, UUID>, JpaSpecificationExecutor<Todo> {

    /**
     * Deletes the todo with the given id.
     *
     * @param id the id of the todo to be deleted
     * @return the number of rows deleted
     */
    long deleteTodoById(UUID id);

    @Query("select t.status as status, count(t) as count from Todo t group by t.status")
    List<StatusCountProjection> countGroupedByStatus();

    @Query("select t.priority as priority, count(t) as count from Todo t group by t.priority")
    List<PriorityCountProjection> countGroupedByPriority();

    @Query("""
            select count(t)
            from Todo t
            where t.dueDate < :now
              and t.status not in :excludedStatuses
            """)
    long countOverdue(
            @Param("now") LocalDateTime now,
            @Param("excludedStatuses") Collection<TaskStatus> excludedStatuses
    );

    @Query("""
            select count(t)
            from Todo t
            where t.dueDate >= :start
              and t.dueDate < :end
            """)
    long countDueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
