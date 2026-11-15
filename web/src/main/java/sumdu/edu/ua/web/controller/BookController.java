package sumdu.edu.ua.web.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.service.BookService;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.web.dto.BookDetailsResponse;
import sumdu.edu.ua.web.util.JsonResponseWriter;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "BookController", urlPatterns = {"/books", "/books/*"})
public class BookController extends HttpServlet {
    private BookService bookService;
    private CommentService commentService;

    @Override
    public void init() throws ServletException {
        this.bookService = (BookService) getServletContext().getAttribute("bookService");
        this.commentService = (CommentService) getServletContext().getAttribute("commentService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                handleListBooks(req, resp);
            } else {
                handleGetBookById(pathInfo, resp);
            }
        } catch (Exception ex) {
            JsonResponseWriter.handleException(req, resp, ex);
        }
    }

    private void handleListBooks(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String q = req.getParameter("q");
        int page = parseIntParam(req.getParameter("page"), 0, "page");
        int size = parseIntParam(req.getParameter("size"), 10, "size");
        String sort = req.getParameter("sort");

        Page<Book> result = bookService.searchBooks(q, page, size, sort);
        JsonResponseWriter.writeJson(resp, HttpServletResponse.SC_OK, result);
    }

    private void handleGetBookById(String pathInfo, HttpServletResponse resp) throws IOException {
        String idStr = pathInfo.substring(1);
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("Некоректний числовий формат ID книги: " + idStr);
        }

        Book book = bookService.getBookById(id);
        List<Comment> comments = commentService.getCommentsByBookId(id);
        JsonResponseWriter.writeJson(resp, HttpServletResponse.SC_OK, new BookDetailsResponse(book, comments));
    }

    private int parseIntParam(String value, int defaultVal, String paramName) {
        if (value == null || value.isBlank()) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Параметр '" + paramName + "' має бути цілим числом");
        }
    }
}
