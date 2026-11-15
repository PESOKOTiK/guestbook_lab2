package sumdu.edu.ua.persistence.repository;

import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.persistence.db.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcCommentRepository implements CommentRepositoryPort {
    private final DatabaseManager dbManager;

    public JdbcCommentRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {
        String sql = "SELECT id, book_id, author, text, created_at FROM comments WHERE book_id = ? ORDER BY created_at DESC";
        List<Comment> comments = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching comments for book " + bookId, e);
        }
        return comments;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT id, book_id, author, text, created_at FROM comments WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding comment " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Comment save(Comment comment) {
        String sql = "INSERT INTO comments (book_id, author, text, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, comment.getBookId());
            ps.setString(2, comment.getAuthor());
            ps.setString(3, comment.getText());
            ps.setTimestamp(4, Timestamp.from(comment.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    comment.setId(keys.getLong(1));
                }
            }
            return comment;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving comment", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM comments WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting comment " + id, e);
        }
    }

    private Comment mapRow(ResultSet rs) throws SQLException {
        return new Comment(
                rs.getLong("id"),
                rs.getLong("book_id"),
                rs.getString("author"),
                rs.getString("text"),
                rs.getTimestamp("created_at").toInstant()
        );
    }
}
