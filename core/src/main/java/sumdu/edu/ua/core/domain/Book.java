package sumdu.edu.ua.core.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class Book {
    private Long id;
    private String title;
    private String author;
    private Integer year;
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
        this.year = (publishedAt != null)
                ? publishedAt.atZone(ZoneOffset.UTC).getYear()
                : LocalDate.now().getYear();
    }

    public Book(Long id, String title, String author, Integer year, String description, Instant publishedAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.description = description;
        this.publishedAt = publishedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public Integer getYear() {
        if (year != null) {
            return year;
        }
        if (publishedAt != null) {
            return publishedAt.atZone(ZoneOffset.UTC).getYear();
        }
        return LocalDate.now().getYear();
    }
    public void setYear(Integer year) {
        this.year = year;
        if (year != null && this.publishedAt == null) {
            this.publishedAt = LocalDate.of(year, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
        }
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Instant getPublishedAt() {
        if (publishedAt != null) {
            return publishedAt;
        }
        int y = (year != null) ? year : LocalDate.now().getYear();
        return LocalDate.of(y, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant();
    }
    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
        if (publishedAt != null && this.year == null) {
            this.year = publishedAt.atZone(ZoneOffset.UTC).getYear();
        }
    }
}
