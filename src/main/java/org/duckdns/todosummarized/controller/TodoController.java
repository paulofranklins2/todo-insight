package org.duckdns.todosummarized.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.duckdns.todosummarized.domains.entity.Todo;
import org.duckdns.todosummarized.domains.enums.TaskPriority;
import org.duckdns.todosummarized.domains.enums.TaskStatus;
import org.duckdns.todosummarized.dto.TodoMapper;
import org.duckdns.todosummarized.dto.TodoRequestDTO;
import org.duckdns.todosummarized.dto.TodoResponseDTO;
import org.duckdns.todosummarized.repository.TodoQuery;
import org.duckdns.todosummarized.service.TodoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST Controller for Todo CRUD operations.
 */
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * Create a new todo.
     */
    @PostMapping
    public ResponseEntity<TodoResponseDTO> createTodo(@Valid @RequestBody TodoRequestDTO request) {
        Todo created = todoService.createTodo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(TodoMapper.toResponseDTO(created));
    }

    /**
     * Get a todo by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponseDTO> getTodoById(@PathVariable UUID id) {
        Todo todo = todoService.getTodoById(id);
        return ResponseEntity.ok(TodoMapper.toResponseDTO(todo));
    }

    /**
     * Update an existing todo.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponseDTO> updateTodo(
            @PathVariable UUID id,
            @Valid @RequestBody TodoRequestDTO request) {
        Todo updated = todoService.updateTodo(id, request);
        return ResponseEntity.ok(TodoMapper.toResponseDTO(updated));
    }

    /**
     * Delete a todo by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable UUID id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Update only the status of a todo.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TodoResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestParam TaskStatus status) {
        Todo updated = todoService.updateStatus(id, status);
        return ResponseEntity.ok(TodoMapper.toResponseDTO(updated));
    }

    /**
     * Search todos with optional filters and pagination.
     */
    @GetMapping
    public ResponseEntity<Page<TodoResponseDTO>> searchTodos(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) LocalDateTime dueFrom,
            @RequestParam(required = false) LocalDateTime dueTo,
            @RequestParam(required = false) Boolean overdue,
            @RequestParam(required = false) Boolean upcoming,
            Pageable pageable) {

        TodoQuery query = new TodoQuery(status, priority, dueFrom, dueTo, overdue, upcoming);
        Page<Todo> todos = todoService.search(query, pageable);
        Page<TodoResponseDTO> response = todos.map(TodoMapper::toResponseDTO);

        return ResponseEntity.ok(response);
    }
}

