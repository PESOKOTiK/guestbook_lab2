package sumdu.edu.ua.core.domain;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private Book book;
    private User user;
    private String text;
    private LocalDateTime createdAt;

    public Comment() {
    }

    public Comment(Long id, Book book, User user, String text, LocalDateTime createdAt) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
