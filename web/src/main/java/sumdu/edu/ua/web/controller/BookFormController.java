package sumdu.edu.ua.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.service.BookService;
import sumdu.edu.ua.web.mail.MailService;

@Controller
public class BookFormController {
    private static final Logger log = LoggerFactory.getLogger(BookFormController.class);

    private final BookService bookService;
    private final MailService mailService;

    public BookFormController(BookService bookService, MailService mailService) {
        this.bookService = bookService;
        this.mailService = mailService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String listBooksPage(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            Model model) {
        Page<Book> booksPage = bookService.searchBooks(q, page, size, sort);
        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("page", booksPage);
        return "books";
    }

    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-add";
    }

    @PostMapping("/books/add")
    public String addBook(@ModelAttribute Book book) {
        Book saved = bookService.addBook(book);
        try {
            mailService.sendNewBookEmail(saved);
        } catch (Exception e) {
            // Логуємо помилку SMTP (наприклад, якщо не введено реальний App Password), не перериваючи редірект
            log.warn("Book saved (id={}), but SMTP notification failed: {}", saved.getId(), e.getMessage());
        }
        return "redirect:/books";
    }
}
