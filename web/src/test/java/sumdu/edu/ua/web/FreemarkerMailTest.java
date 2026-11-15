package sumdu.edu.ua.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.web.mail.MailService;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringBootBookApp.class)
class FreemarkerMailTest {

    @Autowired
    private MailService mailService;

    @Test
    void shouldRenderRareBookBadgeWhenYearIsBefore2000() {
        Book rareBook = new Book(10L, "The C Programming Language", "Brian Kernighan, Dennis Ritchie", 1978, "Classic", Instant.now());
        String html = mailService.renderNewBookHtml(rareBook);

        assertTrue(html.contains("The C Programming Language"));
        assertTrue(html.contains("Brian Kernighan, Dennis Ritchie"));
        assertTrue(html.contains("1978"));
        assertTrue(html.contains("Раритетне видання!"), "Template must show rare edition block for year < 2000");
        assertTrue(html.contains("BookApp &copy; 2025"), "Template must contain custom footer");
        assertTrue(html.contains("raw.githubusercontent.com/VladPiatachenko/MPF_Labs/lab7-freemaker/web/src/main/resources/static/img/logo.png"));
    }

    @Test
    void shouldNotRenderRareBookBadgeWhenYearIs2000OrLater() {
        Book modernBook = new Book(11L, "Clean Code", "Robert C. Martin", 2008, "Modern", Instant.now());
        String html = mailService.renderNewBookHtml(modernBook);

        assertTrue(html.contains("Clean Code"));
        assertFalse(html.contains("Раритетне видання!"), "Template must NOT show rare edition block for year >= 2000");
    }
}
