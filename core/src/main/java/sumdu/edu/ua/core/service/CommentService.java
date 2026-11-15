package sumdu.edu.ua.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.exception.BusinessRuleConflictException;
import sumdu.edu.ua.core.exception.NotFoundException;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.port.UserRepositoryPort;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepositoryPort commentRepository;
    private final CatalogRepositoryPort catalogRepository;
    private final UserRepositoryPort userRepository;

    public CommentService(CommentRepositoryPort commentRepository,
                          CatalogRepositoryPort catalogRepository,
                          UserRepositoryPort userRepository) {
        this.commentRepository = commentRepository;
        this.catalogRepository = catalogRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByBook(Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new ValidationException("Некоректний ID книги");
        }
        return commentRepository.findByBookId(bookId);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Некоректний ID користувача");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Користувача з ID " + userId + " не знайдено"));
        return commentRepository.findByUserId(userId);
    }

    @Transactional
    public Comment addComment(Long bookId, Long userId, String text) {
        if (bookId == null || bookId <= 0) {
            throw new ValidationException("ID книги є обов'язковим");
        }
        if (userId == null || userId <= 0) {
            throw new ValidationException("ID користувача є обов'язковим");
        }
        if (text == null || text.trim().isEmpty() || text.trim().length() > 1000) {
            throw new ValidationException("Текст відгуку має містити від 1 до 1000 символів");
        }

        Book book = catalogRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Книгу з ID " + bookId + " не знайдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Користувача з ID " + userId + " не знайдено"));

        Comment comment = new Comment(null, book, user, text.trim(), LocalDateTime.now());
        Comment saved = commentRepository.save(comment);
        log.info("Comment saved via ORM: commentId={}, bookId={}, userId={}, username='{}'",
                saved.getId(), book.getId(), user.getId(), user.getUsername());
        return saved;
    }

    @Transactional
    public void deleteComment(Long commentId) {
        if (commentId == null || commentId <= 0) {
            throw new ValidationException("Некоректний ID коментаря");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Коментар з ID " + commentId + " не знайдено"));

        Duration age = Duration.between(comment.getCreatedAt(), LocalDateTime.now());
        if (age.toHours() >= 24) {
            throw new BusinessRuleConflictException(
                    "Видалення відгуку заборонено: минуло більше 24 годин (commentId=" + commentId + ")"
            );
        }

        commentRepository.deleteById(commentId);
        log.info("Comment deleted via ORM: commentId={}", commentId);
    }
}
