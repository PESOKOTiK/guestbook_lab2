package sumdu.edu.ua.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.service.BookService;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.web.dto.BookDetailsResponse;
import sumdu.edu.ua.web.dto.CreateBookRequest;

import java.util.List;
import java.util.Map;

/**
 * Контролер каталогу книг (замінює BooksServlet та BooksApiServlet).
 * Демонструє класичний підхід Spring MVC через @Controller + @ResponseBody.
 */
@Controller
@ResponseBody
@RequestMapping({"/books", "/api/books"})
public class BookController {

    private final BookService bookService;
    private final CommentService commentService;

    @Autowired
    @Qualifier("catalogMetadataBean")
    private Map<String, Object> catalogMetadata;

    @Value("${catalog.default-page-size:10}")
    private int defaultPageSize;

    public BookController(BookService bookService, CommentService commentService) {
        this.bookService = bookService;
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<Page<Book>> listBooks(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id") String sort) {
        int effectiveSize = (size != null) ? size : defaultPageSize;
        Page<Book> result = bookService.searchBooks(q, page, effectiveSize, sort);
        return ResponseEntity.ok(result);
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
    public ResponseEntity<Book> createBook(@RequestBody CreateBookRequest req) {
        Book created = bookService.addBook(req.getTitle(), req.getAuthor(), req.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getCatalogInfo() {
        return ResponseEntity.ok(catalogMetadata);
    }
}
