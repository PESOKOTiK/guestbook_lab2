package sumdu.edu.ua.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.BookService;
import sumdu.edu.ua.core.service.CatalogAuditComponent;
import sumdu.edu.ua.core.service.CommentService;

import java.time.Clock;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SpringBootBookApp.class)
class SpringContextTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private BookService bookService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CatalogAuditComponent auditComponent;

    @Autowired
    private CatalogRepositoryPort catalogRepository;

    @Autowired
    private CommentRepositoryPort commentRepository;

    @Autowired
    private Clock appClock;

    @Test
    void shouldLoadAllSpringBeansAndVerifyDependencyInjection() {
        assertNotNull(context);
        assertNotNull(bookService, "@Service BookService should be injected");
        assertNotNull(commentService, "@Service CommentService should be injected");
        assertNotNull(auditComponent, "@Component CatalogAuditComponent should be injected");
        assertNotNull(catalogRepository, "@Repository JdbcCatalogRepository should be injected");
        assertNotNull(commentRepository, "@Repository JdbcCommentRepository should be injected");
        assertNotNull(appClock, "Custom @Bean Clock should be injected");

        assertEquals(24, auditComponent.getMaxDeleteHours());
        assertTrue(bookService.searchBooks(null, 0, 10, "id").getTotalElements() >= 5);

        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) context.getBean("catalogMetadataBean");
        assertEquals("book-catalog-spring-boot", metadata.get("applicationName"));
    }
}
