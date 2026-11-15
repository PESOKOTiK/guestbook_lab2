package sumdu.edu.ua.persistence.adapter;

import org.springframework.stereotype.Component;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.persistence.entity.BookEntity;
import sumdu.edu.ua.persistence.entity.CommentEntity;
import sumdu.edu.ua.persistence.entity.UserEntity;
import sumdu.edu.ua.persistence.repository.BookRepository;
import sumdu.edu.ua.persistence.repository.CommentRepository;
import sumdu.edu.ua.persistence.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaCommentRepositoryAdapter implements CommentRepositoryPort {
    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public JpaCommentRepositoryAdapter(CommentRepository commentRepository,
                                       BookRepository bookRepository,
                                       UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        return commentRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Comment save(Comment comment) {
        BookEntity bookEntity = bookRepository.findById(comment.getBook().getId())
                .orElseThrow(() -> new IllegalArgumentException("Book entity not found: " + comment.getBook().getId()));
        UserEntity userEntity = userRepository.findById(comment.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User entity not found: " + comment.getUser().getId()));

        CommentEntity entity = new CommentEntity(null, bookEntity, userEntity, comment.getText(), comment.getCreatedAt());
        bookEntity.addComment(entity);
        userEntity.addComment(entity);

        CommentEntity saved = commentRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    private Comment toDomain(CommentEntity e) {
        Book book = new Book(
                e.getBook().getId(),
                e.getBook().getTitle(),
                e.getBook().getAuthor(),
                e.getBook().getYear(),
                e.getBook().getDescription()
        );
        User user = new User(
                e.getUser().getId(),
                e.getUser().getUsername(),
                null,
                e.getUser().getRole()
        );
        return new Comment(e.getId(), book, user, e.getText(), e.getCreatedAt());
    }
}
