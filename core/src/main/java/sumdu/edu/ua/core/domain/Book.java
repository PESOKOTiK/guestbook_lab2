package sumdu.edu.ua.core.domain;

import java.time.Instant;

public class Book {
    private Long id;
    private String title;
    private String author;
    private String description;
    private Instant publishedAt;

    public Book() {
    }

    public Book(Long id, String title, String author, String description, Instant publishedAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.publishedAt = publishedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
}
