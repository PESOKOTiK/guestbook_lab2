package sumdu.edu.ua.persistence.repository;

import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.persistence.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcCatalogRepository implements CatalogRepositoryPort {
    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "id", "id",
            "title", "title",
            "author", "author",
            "publishedAt", "published_at"
    );

    private final DatabaseManager dbManager;

    public JdbcCatalogRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Page<Book> findBooks(String query, PageRequest pageRequest) {
        boolean hasQuery = query != null && !query.trim().isEmpty();
        String searchPattern = hasQuery ? "%" + query.trim().toLowerCase() + "%" : null;

        String whereClause = hasQuery
                ? " WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? "
                : "";

        String sortColumn = SORT_COLUMNS.getOrDefault(pageRequest.getSort(), "id");
        String countSql = "SELECT COUNT(*) FROM books" + whereClause;
        String dataSql = "SELECT id, title, author, description, published_at FROM books"
                + whereClause
                + " ORDER BY " + sortColumn + " ASC LIMIT ? OFFSET ?";

        long totalElements = 0;
        List<Book> books = new ArrayList<>();

        try (Connection conn = dbManager.getConnection()) {
            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                if (hasQuery) {
                    countStmt.setString(1, searchPattern);
                    countStmt.setString(2, searchPattern);
                }
                try (ResultSet rs = countStmt.executeQuery()) {
                    if (rs.next()) {
                        totalElements = rs.getLong(1);
                    }
                }
            }

            try (PreparedStatement dataStmt = conn.prepareStatement(dataSql)) {
                int idx = 1;
                if (hasQuery) {
                    dataStmt.setString(idx++, searchPattern);
                    dataStmt.setString(idx++, searchPattern);
                }
                dataStmt.setInt(idx++, pageRequest.getSize());
                dataStmt.setInt(idx, pageRequest.getOffset());

                try (ResultSet rs = dataStmt.executeQuery()) {
                    while (rs.next()) {
                        books.add(mapRow(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying books from database", e);
        }

        return new Page<>(books, pageRequest.getPage(), pageRequest.getSize(), totalElements);
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = "SELECT id, title, author, description, published_at FROM books WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding book by ID " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Book save(Book book) {
        String sql = "INSERT INTO books (title, author, description, published_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getDescription());
            ps.setTimestamp(4, Timestamp.from(book.getPublishedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    book.setId(keys.getLong(1));
                }
            }
            return book;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving book", e);
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("description"),
                rs.getTimestamp("published_at").toInstant()
        );
    }
}
