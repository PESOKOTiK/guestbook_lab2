package sumdu.edu.ua.core.port;

import sumdu.edu.ua.core.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryPort {
    List<Comment> findByBookId(Long bookId);
    List<Comment> findByUserId(Long userId);
    Optional<Comment> findById(Long id);
    Comment save(Comment comment);
    void deleteById(Long id);
}
