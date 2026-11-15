package sumdu.edu.ua.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.service.BookService;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.core.service.UserService;

@Controller
public class BookController {

    private final BookService bookService;
    private final CommentService commentService;
    private final UserService userService;

    public BookController(BookService bookService, CommentService commentService, UserService userService) {
        this.bookService = bookService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("users", userService.findAll());
        return "books";
    }

    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-add";
    }

    @PostMapping("/books/add")
    public String addBook(@ModelAttribute Book book) {
        bookService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/books/{id}")
    public String bookDetails(@PathVariable("id") Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        model.addAttribute("comments", commentService.getCommentsByBook(id));
        model.addAttribute("users", userService.findAll());
        return "book-details";
    }

    @PostMapping("/books/{id}/comments")
    public String addCommentToBook(@PathVariable("id") Long bookId,
                                   @RequestParam("userId") Long userId,
                                   @RequestParam("text") String text) {
        commentService.addComment(bookId, userId, text);
        return "redirect:/books/" + bookId;
    }
}
