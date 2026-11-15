package sumdu.edu.ua.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.exception.BusinessRuleConflictException;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.port.UserRepositoryPort;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {
    private CommentService commentService;
    private Map<Long, Comment> store;

    @BeforeEach
    void setUp() {
        store = new HashMap<>();
        CommentRepositoryPort commentRepo = new CommentRepositoryPort() {
            @Override
            public List<Comment> findByBookId(Long bookId) { return List.copyOf(store.values()); }
            @Override
            public List<Comment> findByUserId(Long userId) { return List.copyOf(store.values()); }
            @Override
            public Optional<Comment> findById(Long id) { return Optional.ofNullable(store.get(id)); }
            @Override
            public Comment save(Comment comment) {
                comment.setId((long) (store.size() + 1));
                store.put(comment.getId(), comment);
                return comment;
            }
            @Override
            public void deleteById(Long id) { store.remove(id); }
        };

        CatalogRepositoryPort catalogRepo = new CatalogRepositoryPort() {
            @Override
            public List<Book> findAll() { return List.of(); }
            @Override
            public Optional<Book> findById(Long id) {
                return Optional.of(new Book(id, "Test Book", "Author", 2020, "Desc"));
            }
            @Override
            public Book save(Book book) { return book; }
            @Override
            public void deleteById(Long id) {}
        };

        UserRepositoryPort userRepo = new UserRepositoryPort() {
            @Override
            public List<User> findAll() { return List.of(); }
            @Override
            public Optional<User> findById(Long id) {
                return Optional.of(new User(id, "pesokotik", "pass", "ROLE_USER"));
            }
            @Override
            public Optional<User> findByUsername(String username) { return Optional.empty(); }
            @Override
            public User save(User user) { return user; }
        };

        commentService = new CommentService(commentRepo, catalogRepo, userRepo);
    }

    @Test
    void shouldAllowDeletingCommentWithin24Hours() {
        Book b = new Book(1L, "Clean Architecture", "Martin", 2017, "");
        User u = new User(1L, "pesokotik", "1234", "ROLE_USER");
        Comment recent = new Comment(1L, b, u, "Свіжий коментар", LocalDateTime.now().minusHours(2));
        store.put(1L, recent);

        assertDoesNotThrow(() -> commentService.deleteComment(1L));
        assertFalse(store.containsKey(1L));
    }

    @Test
    void shouldForbidDeletingCommentOlderThan24Hours() {
        Book b = new Book(1L, "Clean Architecture", "Martin", 2017, "");
        User u = new User(1L, "pesokotik", "1234", "ROLE_USER");
        Comment old = new Comment(2L, b, u, "Старий коментар", LocalDateTime.now().minusHours(25));
        store.put(2L, old);

        assertThrows(BusinessRuleConflictException.class, () -> commentService.deleteComment(2L));
        assertTrue(store.containsKey(2L));
    }
}
