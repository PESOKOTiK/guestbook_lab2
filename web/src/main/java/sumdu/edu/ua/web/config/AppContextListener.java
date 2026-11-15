package sumdu.edu.ua.web.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.BookService;
import sumdu.edu.ua.core.service.CommentService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Використовуємо рефлексію у точці композиції, щоб модуль web не мав прямої compile-time залежності від класів persistence
            Class<?> dbManagerClass = Class.forName("sumdu.edu.ua.persistence.db.DatabaseManager");
            Constructor<?> dbConstructor = dbManagerClass.getConstructor(String.class, String.class, String.class);
            Object dbManager = dbConstructor.newInstance("jdbc:h2:mem:catalogdb;DB_CLOSE_DELAY=-1", "sa", "");

            Method initMethod = dbManagerClass.getMethod("initSchemaAndSeedData");
            initMethod.invoke(dbManager);

            Class<?> catalogRepoClass = Class.forName("sumdu.edu.ua.persistence.repository.JdbcCatalogRepository");
            CatalogRepositoryPort catalogRepo = (CatalogRepositoryPort) catalogRepoClass
                    .getConstructor(dbManagerClass)
                    .newInstance(dbManager);

            Class<?> commentRepoClass = Class.forName("sumdu.edu.ua.persistence.repository.JdbcCommentRepository");
            CommentRepositoryPort commentRepo = (CommentRepositoryPort) commentRepoClass
                    .getConstructor(dbManagerClass)
                    .newInstance(dbManager);

            BookService bookService = new BookService(catalogRepo);
            CommentService commentService = new CommentService(commentRepo, catalogRepo);

            ServletContext ctx = sce.getServletContext();
            ctx.setAttribute("bookService", bookService);
            ctx.setAttribute("commentService", commentService);

            log.info("Application context initialized: BookService and CommentService wired successfully");
        } catch (Exception e) {
            log.error("Failed to wire application dependencies", e);
            throw new RuntimeException("Context initialization failed", e);
        }
    }
}
