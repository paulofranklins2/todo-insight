package org.duckdns.todosummarized.repository;

import org.duckdns.todosummarized.domains.entity.AiInsight;
import org.duckdns.todosummarized.domains.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for AiInsight entity operations.
 */
@Repository
public interface AiInsightRepository extends JpaRepository<AiInsight, UUID> {

    /**
     * Find the AI insight for a specific user.
     */
    Optional<AiInsight> findByUser(User user);

    /**
     * Delete the AI insight by ID and user in a single query.
     */
    void deleteByIdAndUser(UUID id, User user);

}

