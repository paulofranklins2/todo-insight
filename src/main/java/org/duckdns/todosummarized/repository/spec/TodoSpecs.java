package org.duckdns.todosummarized.repository.spec;

import jakarta.persistence.criteria.Predicate;
import lombok.NoArgsConstructor;
import org.duckdns.todosummarized.domains.entity.Todo;
import org.duckdns.todosummarized.repository.TodoQuery;
import org.springframework.data.jpa.domain.Specification;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public final class TodoSpecs {

    public static Specification<Todo> byQuery(TodoQuery todoQuery, Clock clock) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> ps = new ArrayList<>();

            if (todoQuery.status() != null) {
                ps.add(criteriaBuilder.equal(root.get("status"), todoQuery.status()));
            }
            if (todoQuery.priority() != null) {
                ps.add(criteriaBuilder.equal(root.get("priority"), todoQuery.priority()));
            }
            if (todoQuery.dueFrom() != null) {
                ps.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), todoQuery.dueFrom()));
            }
            if (todoQuery.dueTo() != null) {
                ps.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), todoQuery.dueTo()));
            }

            LocalDateTime now = LocalDateTime.now(clock);
            if (Boolean.TRUE.equals(todoQuery.overdue())) {
                ps.add(criteriaBuilder.lessThan(root.get("dueDate"), now));
            }
            if (Boolean.TRUE.equals(todoQuery.upcoming())) {
                ps.add(criteriaBuilder.greaterThan(root.get("dueDate"), now));
            }

            return criteriaBuilder.and(ps.toArray(Predicate[]::new));
        };
    }
}
