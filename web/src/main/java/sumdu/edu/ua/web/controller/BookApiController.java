package sumdu.edu.ua.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.service.BookService;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.web.dto.BookDetailsResponse;
import sumdu.edu.ua.web.mail.MailService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService bookService;
    private final CommentService commentService;
    private final MailService mailService;

    public BookApiController(BookService bookService, CommentService commentService, MailService mailService) {
        this.bookService = bookService;
        this.commentService = commentService;
        this.mailService = mailService;
    }

    @GetMapping
    public ResponseEntity<Page<Book>> listBooks(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort) {
        return ResponseEntity.ok(bookService.searchBooks(q, page, size, sort));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsResponse> getBookById(@PathVariable("id") String rawId) {
        Long id;
        try {
            id = Long.parseLong(rawId);
        } catch (NumberFormatException e) {
            throw new ValidationException("Некоректний числовий формат ID книги: " + rawId);
        }
        Book book = bookService.getBookById(id);
        List<Comment> comments = commentService.getCommentsByBookId(id);
        return ResponseEntity.ok(new BookDetailsResponse(book, comments));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBookAndSendEmail(@RequestBody Book book) {
        Book saved = bookService.addBook(book);
        String renderedHtml = mailService.renderNewBookHtml(saved);
        String mailStatus;
        try {
            mailService.sendNewBookEmail(saved);
            mailStatus = "SENT_OR_PROCESSED";
        } catch (Exception e) {
            mailStatus = "SMTP_ERROR: " + e.getMessage();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "book", saved,
                "mailStatus", mailStatus,
                "renderedEmailHtmlPreview", renderedHtml
        ));
    }
}
