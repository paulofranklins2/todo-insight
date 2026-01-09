package org.duckdns.todosummarized.service;

import lombok.RequiredArgsConstructor;
import org.duckdns.todosummarized.domains.entity.Todo;
import org.duckdns.todosummarized.domains.enums.TaskPriority;
import org.duckdns.todosummarized.domains.enums.TaskStatus;
import org.duckdns.todosummarized.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public Todo create(Todo todo) {
        return todoRepository.save(todo);
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public Optional<Todo> findById(UUID id) {
        return todoRepository.findById(id);
    }

    public List<Todo> findByStatus(TaskStatus status) {
        return todoRepository.findByStatus(status);
    }

    public List<Todo> findByPriority(TaskPriority priority) {
        return todoRepository.findByPriority(priority);
    }

    public List<Todo> findOverdue() {
        return todoRepository.findByDueDateBefore(LocalDateTime.now());
    }

    public List<Todo> findUpcoming() {
        return todoRepository.findByDueDateAfter(LocalDateTime.now());
    }

    public Todo update(Todo todo) {
        return todoRepository.save(todo);
    }

    public Optional<Todo> markCompleted(UUID id) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setStatus(TaskStatus.COMPLETED);
                    return todoRepository.save(todo);
                });
    }

    public Optional<Todo> markInProgress(UUID id) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setStatus(TaskStatus.IN_PROGRESS);
                    return todoRepository.save(todo);
                });
    }

    public void delete(UUID id) {
        todoRepository.deleteById(id);
    }
}
