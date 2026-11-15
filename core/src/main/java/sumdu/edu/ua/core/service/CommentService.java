package sumdu.edu.ua.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.exception.BusinessRuleConflictException;
import sumdu.edu.ua.core.exception.NotFoundException;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class CommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepositoryPort commentRepository;
    private final CatalogRepositoryPort catalogRepository;

    public CommentService(CommentRepositoryPort commentRepository, CatalogRepositoryPort catalogRepository) {
        this.commentRepository = commentRepository;
        this.catalogRepository = catalogRepository;
    }

    public List<Comment> getCommentsByBookId(Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new ValidationException("Некоректний ID книги");
        }
        return commentRepository.findByBookId(bookId);
    }

    public Comment addComment(Long bookId, String author, String text) {
        if (bookId == null || bookId <= 0) {
            throw new ValidationException("ID книги є обов'язковим");
        }
        if (author == null || author.trim().isEmpty() || author.trim().length() > 100) {
            throw new ValidationException("Ім'я автора має містити від 1 до 100 символів");
        }
        if (text == null || text.trim().isEmpty() || text.trim().length() > 1000) {
            throw new ValidationException("Текст відгуку має містити від 1 до 1000 символів");
        }

        catalogRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Книгу з ID " + bookId + " не знайдено"));

        Comment comment = new Comment(null, bookId, author.trim(), text.trim(), Instant.now());
        Comment saved = commentRepository.save(comment);
        log.info("Comment created: commentId={}, bookId={}, author='{}', length={}",
                saved.getId(), saved.getBookId(), saved.getAuthor(), saved.getText().length());
        return saved;
    }

    public void deleteComment(Long commentId) {
        if (commentId == null || commentId <= 0) {
            throw new ValidationException("Некоректний ID коментаря");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Коментар з ID " + commentId + " не знайдено"));

        Duration age = Duration.between(comment.getCreatedAt(), Instant.now());
        if (age.toHours() >= 24) {
            throw new BusinessRuleConflictException(
                    "Видалення відгуку заборонено: минуло більше 24 годин з моменту створення (commentId=" + commentId + ")"
            );
        }

        commentRepository.deleteById(commentId);
        log.info("Comment deleted: commentId={}, bookId={}, author='{}'",
                comment.getId(), comment.getBookId(), comment.getAuthor());
    }
}
