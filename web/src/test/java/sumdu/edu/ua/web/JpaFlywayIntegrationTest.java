package sumdu.edu.ua.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.service.BookService;
import sumdu.edu.ua.core.service.CommentService;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SpringBootBookApp.class)
@AutoConfigureMockMvc
class JpaFlywayIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    @Test
    void flywayMigrationsShouldPopulateBooksUsersAndComments() {
        assertTrue(bookService.findAll().size() >= 5, "Flyway V2__seed_data.sql should seed 5 books");
        List<Comment> user1Comments = commentService.getCommentsByUser(1L);
        assertFalse(user1Comments.isEmpty(), "User 1 (pesokotik) should have seeded comments");
    }

    @Test
    void userCommentsPageShouldRenderSuccessfully() throws Exception {
        mockMvc.perform(get("/users/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Коментарі користувача: pesokotik")))
                .andExpect(content().string(containsString("/books/1")));
    }

    @Test
    void postingCommentViaFormShouldPersistInJpaAndShowOnUserPage() throws Exception {
        mockMvc.perform(post("/books/1/comments")
                        .param("userId", "1")
                        .param("text", "Новий ORM-коментар через Hibernate!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/1"));

        mockMvc.perform(get("/users/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Новий ORM-коментар через Hibernate!")));
    }
}
