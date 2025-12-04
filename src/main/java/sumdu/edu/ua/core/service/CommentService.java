package sumdu.edu.ua.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.time.Duration;
import java.time.Instant;

/**
 * Service class for comment business logic.
 * Demonstrates dependency injection via constructor.
 * 
 * Business rule: A comment can be deleted only within 24 hours from its creation.
 */
@Service
public class CommentService {
    private final CommentRepositoryPort repo;

    /**
     * Constructor-based dependency injection.
     * Spring will automatically inject the CommentRepositoryPort bean.
     */
    @Autowired
    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }

    /**
     * Deletes a comment only if it was created not more than 24 hours ago.
     *
     * @param bookId the book ID
     * @param commentId the comment ID
     * @throws IllegalStateException if the comment was not found or is older than 24 hours
     */
    public void delete(long bookId, long commentId) {
        Comment comment = repo.findById(bookId, commentId);
        if (comment == null) {
            throw new IllegalStateException("The comment was not found");
        }

        Instant createdAt = comment.getCreatedAt();
        if (createdAt == null) {
            throw new IllegalStateException("The time of creation of the comment is unknown");
        }

        if (Duration.between(createdAt, Instant.now()).toHours() > 24) {
            throw new IllegalStateException("You can't delete a comment that is older than 24 hours");
        }

        repo.delete(bookId, commentId);
    }
}

