package sumdu.edu.ua.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import sumdu.edu.ua.web.config.WebConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SpringBootBookApp.class)
@AutoConfigureMockMvc
class SpringMvcIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterDispatcherServletAndEnableWebMvcConfig() {
        assertNotNull(context.getBean(DispatcherServlet.class), "DispatcherServlet must be registered");
        assertNotNull(context.getBean(WebConfig.class), "WebConfig must be registered");
        assertTrue(WebConfig.class.isAnnotationPresent(EnableWebMvc.class), "WebConfig must have @EnableWebMvc");
    }

    @Test
    void getBooksShouldReturnJsonPage() throws Exception {
        mockMvc.perform(get("/books").param("q", "clean").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Clean Architecture"));
    }

    @Test
    void getBookByIdShouldReturnBookWithComments() throws Exception {
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.book.id").value(1))
                .andExpect(jsonPath("$.comments").isArray());
    }

    @Test
    void postCommentShouldCreateNewCommentAndValidateInput() throws Exception {
        String validJson = "{\"bookId\": 1, \"author\": \"Олексій\", \"text\": \"Тестовий відгук через MockMvc\"}";
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value("Олексій"));

        String invalidJson = "{\"bookId\": 1, \"author\": \"\", \"text\": \"\"}";
        mockMvc.perform(post("/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
