package org.duckdns.todosummarized.service;

import lombok.RequiredArgsConstructor;
import org.duckdns.todosummarized.domains.entity.Todo;
import org.duckdns.todosummarized.domains.entity.User;
import org.duckdns.todosummarized.domains.enums.TaskStatus;
import org.duckdns.todosummarized.dto.TodoMapper;
import org.duckdns.todosummarized.repository.TodoQuery;
import org.duckdns.todosummarized.dto.TodoRequestDTO;
import org.duckdns.todosummarized.exception.TodoNotFoundException;
import org.duckdns.todosummarized.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service layer for Todo operations.
 */
@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final CacheService cacheService;

    /**
     * Create a new todo for the specified user.
     */
    public Todo createTodo(TodoRequestDTO todo, User user) {
        Todo created = todoRepository.save(TodoMapper.toNewEntity(todo, user));
        cacheService.evictTodosByUser(user.getId());
        return created;
    }

    /**
     * Get a todo by ID for the specified user.
     */
    @Transactional(readOnly = true)
    public Todo getTodoById(UUID id, User user) {
        return todoRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    /**
     * Update an existing todo for the specified user.
     */
    @Transactional
    public Todo updateTodo(UUID id, TodoRequestDTO updatedTodo, User user) {
        Todo existingTodo = getTodoById(id, user);
        TodoMapper.patchEntity(updatedTodo, existingTodo);
        cacheService.evictTodosByUser(user.getId());
        return existingTodo;
    }

    /**
     * Delete a todo by its ID for the specified user.
     */
    @Transactional
    public void deleteTodo(UUID id, User user) {
        long deleted = todoRepository.deleteByIdAndUser(id, user);

        if (deleted == 0) {
            throw new TodoNotFoundException(id);
        }
        cacheService.evictTodosByUser(user.getId());
    }

    /**
     * Update the status of a todo for the specified user.
     */
    @Transactional
    public Todo updateStatus(UUID id, TaskStatus status, User user) {
        Todo todo = getTodoById(id, user);
        todo.setStatus(status);
        cacheService.evictTodosByUser(user.getId());
        return todo;
    }

    /**
     * Search for todos based on the given query, scoped to the specified user.
     */
    @Transactional(readOnly = true)
    public Page<Todo> search(TodoQuery query, Pageable pageable, User user) {
        return cacheService.searchTodos(query, pageable, user);
    }
}