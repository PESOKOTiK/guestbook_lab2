package sumdu.edu.ua.web.dto;

import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;

import java.util.List;

public class BookDetailsResponse {
    private final Book book;
    private final List<Comment> comments;

    public BookDetailsResponse(Book book, List<Comment> comments) {
        this.book = book;
        this.comments = comments;
    }

    public Book getBook() { return book; }
    public List<Comment> getComments() { return comments; }
}
