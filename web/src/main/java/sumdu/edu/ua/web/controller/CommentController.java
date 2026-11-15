package sumdu.edu.ua.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.web.dto.CreateCommentRequest;
import sumdu.edu.ua.web.util.JsonResponseWriter;

import java.io.IOException;
import java.util.Map;

@WebServlet(name = "CommentController", urlPatterns = {"/comments", "/comments/*"})
public class CommentController extends HttpServlet {
    private CommentService commentService;

    @Override
    public void init() throws ServletException {
        this.commentService = (CommentService) getServletContext().getAttribute("commentService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long bookId;
            String author;
            String text;

            String contentType = req.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                CreateCommentRequest body = JsonResponseWriter.getMapper()
                        .readValue(req.getInputStream(), CreateCommentRequest.class);
                bookId = body.getBookId();
                author = body.getAuthor();
                text = body.getText();
            } else {
                String bookIdParam = req.getParameter("bookId");
                if (bookIdParam == null) {
                    throw new ValidationException("Параметр 'bookId' є обов'язковим");
                }
                try {
                    bookId = Long.parseLong(bookIdParam);
                } catch (NumberFormatException e) {
                    throw new ValidationException("Некоректний формат 'bookId'");
                }
                author = req.getParameter("author");
                text = req.getParameter("text");
            }

            Comment created = commentService.addComment(bookId, author, text);
            JsonResponseWriter.writeJson(resp, HttpServletResponse.SC_CREATED, created);
        } catch (Exception ex) {
            JsonResponseWriter.handleException(req, resp, ex);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            Long commentId = null;

            if (pathInfo != null && pathInfo.length() > 1) {
                try {
                    commentId = Long.parseLong(pathInfo.substring(1));
                } catch (NumberFormatException e) {
                    throw new ValidationException("Некоректний ID коментаря у шляху: " + pathInfo.substring(1));
                }
            } else if (req.getParameter("id") != null) {
                try {
                    commentId = Long.parseLong(req.getParameter("id"));
                } catch (NumberFormatException e) {
                    throw new ValidationException("Некоректний параметр 'id'");
                }
            } else {
                throw new ValidationException("Не вказано ID коментаря для видалення");
            }

            commentService.deleteComment(commentId);
            JsonResponseWriter.writeJson(resp, HttpServletResponse.SC_OK,
                    Map.of("status", "deleted", "commentId", commentId));
        } catch (Exception ex) {
            JsonResponseWriter.handleException(req, resp, ex);
        }
    }
}
