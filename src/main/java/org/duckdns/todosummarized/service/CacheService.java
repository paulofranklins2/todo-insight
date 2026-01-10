package org.duckdns.todosummarized.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.duckdns.todosummarized.domains.entity.Todo;
import org.duckdns.todosummarized.domains.entity.User;
import org.duckdns.todosummarized.repository.TodoQuery;
import org.duckdns.todosummarized.repository.TodoRepository;
import org.duckdns.todosummarized.repository.UserRepository;
import org.duckdns.todosummarized.repository.spec.TodoSpecs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

/**
 * Unified caching service with for Users and Todos.
 * Uses Caffeine cache backed by ConcurrentHashMap for thread-safe operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final Cache<String, User> userCache;
    private final Cache<String, Page<Todo>> todoSearchCache;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final Clock clock;

    /**
     * Get a user by email with cache lookup.
     * Falls back to database if not in cache.
     */
    public Optional<User> findUserByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        String normalizedEmail = email.toLowerCase();

        User cachedUser = userCache.getIfPresent(normalizedEmail);
        if (cachedUser != null) {
            log.info("USER CACHE HIT: {} (no database query)", normalizedEmail);
            return Optional.of(cachedUser);
        }

        log.info("USER CACHE MISS: {} (fetching from database)", normalizedEmail);
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);
        userOpt.ifPresent(user -> userCache.put(normalizedEmail, user));

        return userOpt;
    }

    /**
     * Put a user into the cache.
     */
    public void putUser(User user) {
        if (user != null && user.getEmail() != null) {
            userCache.put(user.getEmail().toLowerCase(), user);
            log.debug("Cached user: {}", user.getEmail());
        }
    }

    /**
     * Invalidate a user from cache.
     */
    public void evictUser(String email) {
        if (email != null) {
            userCache.invalidate(email.toLowerCase());
            log.debug("Evicted user from cache: {}", email);
        }
    }

    /**
     * Search todos with cache lookup.
     * Falls back to database if not in cache.
     */
    public Page<Todo> searchTodos(TodoQuery query, Pageable pageable, User user) {
        String cacheKey = buildTodoCacheKey(user.getId(), query, pageable);

        Page<Todo> cachedResult = todoSearchCache.getIfPresent(cacheKey);
        if (cachedResult != null) {
            log.info("TODO CACHE HIT: {} (no database query)", cacheKey);
            return cachedResult;
        }

        log.info("TODO CACHE MISS: {} (fetching from database)", cacheKey);
        Page<Todo> result = todoRepository.findAll(
                TodoSpecs.byQueryAndUser(query, clock, user),
                pageable
        );
        todoSearchCache.put(cacheKey, result);

        return result;
    }

    /**
     * Invalidate all cached todos for a specific user.
     * Call this when any todo is created, updated, or deleted.
     */
    public void evictTodosByUser(UUID userId) {
        if (userId != null) {
            todoSearchCache.asMap().keySet().removeIf(key -> key.startsWith(userId.toString()));
            log.info("TODO CACHE EVICTED: all entries for user {}", userId);
        }
    }

    /**
     * Clear all caches.
     */
    public void clearAll() {
        userCache.invalidateAll();
        todoSearchCache.invalidateAll();
        log.info("ALL CACHES CLEARED");
    }

    /**
     * Get cache statistics for monitoring.
     */
    public String getStats() {
        return String.format("UserCache: %s | TodoCache: %s",
                userCache.stats().toString(),
                todoSearchCache.stats().toString());
    }

    private String buildTodoCacheKey(UUID userId, TodoQuery query, Pageable pageable) {
        return String.format("%s|%s|%s|%d|%d|%s",
                userId,
                query.status(),
                query.priority(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString()
        );
    }
}

