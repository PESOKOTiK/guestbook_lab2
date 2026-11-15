package sumdu.edu.ua.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.exception.BusinessRuleConflictException;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
            public Page<Book> findBooks(String query, PageRequest pageRequest) { return null; }
            @Override
            public Optional<Book> findById(Long id) {
                return Optional.of(new Book(id, "Test Book", "Author", "Desc", Instant.now()));
            }
            @Override
            public Book save(Book book) { return book; }
        };

        commentService = new CommentService(commentRepo, catalogRepo);
    }

    @Test
    void shouldAllowDeletingCommentWithin24Hours() {
        Comment recent = new Comment(1L, 1L, "Олексій", "Чудова книга!", Instant.now().minus(2, ChronoUnit.HOURS));
        store.put(1L, recent);

        assertDoesNotThrow(() -> commentService.deleteComment(1L));
        assertFalse(store.containsKey(1L));
    }

    @Test
    void shouldForbidDeletingCommentOlderThan24Hours() {
        Comment old = new Comment(2L, 1L, "Олексій", "Старий коментар", Instant.now().minus(25, ChronoUnit.HOURS));
        store.put(2L, old);

        assertThrows(BusinessRuleConflictException.class, () -> commentService.deleteComment(2L));
        assertTrue(store.containsKey(2L));
    }
}
