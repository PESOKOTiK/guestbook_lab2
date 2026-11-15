package sumdu.edu.ua.web.dto;

public class CreateCommentRequest {
    private Long bookId;
    private String author;
    private String text;

    public CreateCommentRequest() {
    }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
