package sumdu.edu.ua.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.web.dto.CreateCommentRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getCommentsByBook(@RequestParam("bookId") Long bookId) {
        return ResponseEntity.ok(commentService.getCommentsByBookId(bookId));
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CreateCommentRequest req) {
        Comment created = commentService.addComment(req.getBookId(), req.getAuthor(), req.getText());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable("id") String rawId) {
        Long commentId;
        try {
            commentId = Long.parseLong(rawId);
        } catch (NumberFormatException e) {
            throw new ValidationException("Некоректний ID коментаря: " + rawId);
        }
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of("status", "deleted", "commentId", commentId));
    }
}
