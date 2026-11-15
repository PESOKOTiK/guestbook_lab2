package sumdu.edu.ua.persistence.db;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Component
public class DatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private final String url;
    private final String user;
    private final String password;

    @Autowired
    public DatabaseManager(
            @Value("${spring.datasource.url:jdbc:h2:mem:catalogdb;DB_CLOSE_DELAY=-1}") String url,
            @Value("${spring.datasource.username:sa}") String user,
            @Value("${spring.datasource.password:}") String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @PostConstruct
    public void initSchemaAndSeedData() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql");
            if (is != null) {
                String sql = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                        .lines().collect(Collectors.joining("\n"));
                stmt.execute(sql);
            }

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM books")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    seedInitialData(conn);
                }
            }
            log.info("H2 database schema initialized via @PostConstruct lifecycle callback (url={})", url);
        } catch (Exception e) {
            log.error("Failed to initialize H2 database schema", e);
            throw new RuntimeException("Database initialization error", e);
        }
    }

    private void seedInitialData(Connection conn) throws SQLException {
        String insertBook = "INSERT INTO books (title, author, pub_year, description, published_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertBook)) {
            Object[][] books = {
                {"Clean Architecture", "Robert C. Martin", 2017, "A Craftsman's Guide to Software Structure and Design", Instant.parse("2017-09-10T00:00:00Z")},
                {"The Art of Computer Programming", "Donald E. Knuth", 1968, "Fundamental Algorithms (Rare Classic Edition)", Instant.parse("1968-01-01T00:00:00Z")},
                {"Designing Data-Intensive Applications", "Martin Kleppmann", 2017, "The Big Ideas Behind Reliable, Scalable, and Maintainable Systems", Instant.parse("2017-03-16T00:00:00Z")},
                {"Design Patterns (GoF)", "Erich Gamma et al.", 1994, "Elements of Reusable Object-Oriented Software (Rare Edition)", Instant.parse("1994-10-21T00:00:00Z")},
                {"Effective Java", "Joshua Bloch", 2018, "Best practices for the Java platform", Instant.parse("2018-01-06T00:00:00Z")}
            };
            for (Object[] b : books) {
                ps.setString(1, (String) b[0]);
                ps.setString(2, (String) b[1]);
                ps.setInt(3, (Integer) b[2]);
                ps.setString(4, (String) b[3]);
                ps.setTimestamp(5, Timestamp.from((Instant) b[4]));
                ps.executeUpdate();
            }
        }

        String insertComment = "INSERT INTO comments (book_id, author, text, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertComment)) {
            ps.setLong(1, 1L);
            ps.setString(2, "Олексій");
            ps.setString(3, "Свіжий відгук (створений щойно — можна видалити)");
            ps.setTimestamp(4, Timestamp.from(Instant.now().minus(1, ChronoUnit.HOURS)));
            ps.executeUpdate();

            ps.setLong(1, 1L);
            ps.setString(2, "Архіваріус");
            ps.setString(3, "Старий відгук (створений 48 годин тому — видалення поверне 409 Conflict)");
            ps.setTimestamp(4, Timestamp.from(Instant.now().minus(48, ChronoUnit.HOURS)));
            ps.executeUpdate();
        }
    }
}
