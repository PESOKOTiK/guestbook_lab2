package sumdu.edu.ua.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.json.JavalinJackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.exception.BusinessRuleConflictException;
import sumdu.edu.ua.core.exception.NotFoundException;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.BookService;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.web.dto.BookDetailsResponse;
import sumdu.edu.ua.web.dto.CreateCommentRequest;
import sumdu.edu.ua.web.dto.ErrorResponse;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class JavalinBookApp {
    private static final Logger log = LoggerFactory.getLogger(JavalinBookApp.class);

    private BookService bookService;
    private CommentService commentService;

    public static void main(String[] args) {
        JavalinBookApp bookApp = new JavalinBookApp();
        bookApp.initServices();
        Javalin app = bookApp.createApp();
        app.start(8080);
        log.info("JavalinBookApp started on http://localhost:8080");
    }

    public void initServices() {
        try {
            // Ініціалізація адаптерів persistence через рефлексію, щоб модуль web не мав прямої залежності від persistence (вимога ArchUnit)
            Class<?> dbManagerClass = Class.forName("sumdu.edu.ua.persistence.db.DatabaseManager");
            Constructor<?> dbConstructor = dbManagerClass.getConstructor(String.class, String.class, String.class);
            Object dbManager = dbConstructor.newInstance("jdbc:h2:mem:catalogdb;DB_CLOSE_DELAY=-1", "sa", "");

            Method initMethod = dbManagerClass.getMethod("initSchemaAndSeedData");
            initMethod.invoke(dbManager);

            Class<?> catalogRepoClass = Class.forName("sumdu.edu.ua.persistence.repository.JdbcCatalogRepository");
            CatalogRepositoryPort catalogRepo = (CatalogRepositoryPort) catalogRepoClass
                    .getConstructor(dbManagerClass)
                    .newInstance(dbManager);

            Class<?> commentRepoClass = Class.forName("sumdu.edu.ua.persistence.repository.JdbcCommentRepository");
            CommentRepositoryPort commentRepo = (CommentRepositoryPort) commentRepoClass
                    .getConstructor(dbManagerClass)
                    .newInstance(dbManager);

            this.bookService = new BookService(catalogRepo);
            this.commentService = new CommentService(commentRepo, catalogRepo);
            log.info("Javalin services initialized successfully (BookService, CommentService)");
        } catch (Exception e) {
            log.error("Failed to initialize services for JavalinBookApp", e);
            throw new RuntimeException("Application wiring failed", e);
        }
    }

    public Javalin createApp() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper));
            config.http.defaultContentType = "application/json;charset=UTF-8";
        });

        // 1. Middleware (app.before) для логування всіх вхідних HTTP-запитів
        app.before(ctx -> {
            String query = ctx.queryString() != null ? "?" + ctx.queryString() : "";
            log.info("Incoming HTTP request: {} {}{}", ctx.method(), ctx.path(), query);
        });

        // 2. Маршрути каталогу книг та коментарів (app.get, app.post, app.delete)
        app.get("/books", this::handleGetBooks);
        app.get("/books/{id}", this::handleGetBookById);
        app.post("/comments", this::handleCreateComment);
        app.delete("/comments/{id}", this::handleDeleteComment);

        // 3. Централізована обробка винятків через app.exception()
        app.exception(ValidationException.class, (ex, ctx) -> {
            log.warn("Client error 400 Bad Request at {}: {}", ctx.path(), ex.getMessage());
            ctx.status(400).json(new ErrorResponse(400, "Bad Request", ex.getMessage(), ctx.path()));
        });

        app.exception(IllegalArgumentException.class, (ex, ctx) -> {
            log.warn("Client error 400 Bad Request at {}: {}", ctx.path(), ex.getMessage());
            ctx.status(400).json(new ErrorResponse(400, "Bad Request", ex.getMessage(), ctx.path()));
        });

        app.exception(NotFoundException.class, (ex, ctx) -> {
            log.warn("Client error 404 Not Found at {}: {}", ctx.path(), ex.getMessage());
            ctx.status(404).json(new ErrorResponse(404, "Not Found", ex.getMessage(), ctx.path()));
        });

        app.exception(BusinessRuleConflictException.class, (ex, ctx) -> {
            log.warn("Business conflict 409 Conflict at {}: {}", ctx.path(), ex.getMessage());
            ctx.status(409).json(new ErrorResponse(409, "Conflict", ex.getMessage(), ctx.path()));
        });

        app.exception(Exception.class, (ex, ctx) -> {
            log.error("Internal server error 500 at " + ctx.path(), ex);
            ctx.status(500).json(new ErrorResponse(500, "Internal Server Error", "Внутрішня помилка сервера", ctx.path()));
        });

        return app;
    }

    private void handleGetBooks(Context ctx) {
        String q = ctx.queryParam("q");
        int page = parseQueryInt(ctx.queryParam("page"), 0, "page");
        int size = parseQueryInt(ctx.queryParam("size"), 10, "size");
        String sort = ctx.queryParam("sort");

        Page<Book> result = bookService.searchBooks(q, page, size, sort);
        ctx.status(200).json(result);
    }

    private void handleGetBookById(Context ctx) {
        Long id = parsePathId(ctx.pathParam("id"), "ID книги");
        Book book = bookService.getBookById(id);
        List<Comment> comments = commentService.getCommentsByBookId(id);
        ctx.status(200).json(new BookDetailsResponse(book, comments));
    }

    private void handleCreateComment(Context ctx) {
        CreateCommentRequest req;
        try {
            req = ctx.bodyAsClass(CreateCommentRequest.class);
        } catch (Exception e) {
            throw new ValidationException("Некоректне тіло JSON-запиту");
        }

        Comment created = commentService.addComment(req.getBookId(), req.getAuthor(), req.getText());
        ctx.status(201).json(created);
    }

    private void handleDeleteComment(Context ctx) {
        Long commentId = parsePathId(ctx.pathParam("id"), "ID коментаря");
        commentService.deleteComment(commentId);
        ctx.status(200).json(Map.of("status", "deleted", "commentId", commentId));
    }

    private int parseQueryInt(String value, int defaultVal, String paramName) {
        if (value == null || value.isBlank()) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Параметр '" + paramName + "' має бути цілим числом");
        }
    }

    private Long parsePathId(String rawId, String label) {
        try {
            return Long.parseLong(rawId);
        } catch (NumberFormatException e) {
            throw new ValidationException("Некоректний числовий формат (" + label + "): " + rawId);
        }
    }
}
