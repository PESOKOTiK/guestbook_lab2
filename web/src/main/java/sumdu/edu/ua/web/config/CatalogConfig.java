package sumdu.edu.ua.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sumdu.edu.ua.core.service.BookService;
import sumdu.edu.ua.core.service.CatalogAuditComponent;
import sumdu.edu.ua.core.service.CommentService;

import java.time.Clock;
import java.util.Map;

@Configuration
public class CatalogConfig {
    private static final Logger log = LoggerFactory.getLogger(CatalogConfig.class);

    // 1. Кастомний бін Clock через анотацію @Bean
    @Bean
    public Clock appClock() {
        log.info("Creating custom @Bean 'appClock' (UTC system clock)");
        return Clock.systemUTC();
    }

    // 2. Кастомний бін-метадані каталогу з параметрів application.yaml
    @Bean
    public Map<String, Object> catalogMetadataBean(
            @Value("${spring.application.name}") String appName,
            @Value("${server.port}") int serverPort,
            @Value("${catalog.welcome-banner}") String banner,
            @Value("${catalog.default-page-size}") int defaultPageSize,
            @Value("${catalog.comment.delete-window-hours}") int deleteWindowHours) {
        log.info("Creating custom @Bean 'catalogMetadataBean' with banner: '{}'", banner);
        return Map.of(
                "applicationName", appName,
                "serverPort", serverPort,
                "welcomeBanner", banner,
                "defaultPageSize", defaultPageSize,
                "commentDeleteWindowHours", deleteWindowHours
        );
    }

    // 3. Перевірка завантаження бінів та їхньої взаємодії під час старту застосунку
    @Bean
    public CommandLineRunner verifyBeansRunner(
            ApplicationContext ctx,
            BookService bookService,
            CommentService commentService,
            CatalogAuditComponent auditComponent,
            Clock appClock) {
        return args -> {
            log.info("=== [SPRING IoC VERIFICATION] ===");
            log.info("Loaded @Service bean: {} (books in DB: {})",
                    bookService.getClass().getSimpleName(),
                    bookService.searchBooks(null, 0, 10, "id").getTotalElements());
            log.info("Loaded @Service bean: {}", commentService.getClass().getSimpleName());
            log.info("Loaded @Component bean: {} (deleteWindowHours={})",
                    auditComponent.getClass().getSimpleName(), auditComponent.getMaxDeleteHours());
            log.info("Loaded custom @Bean Clock: {} (current time={})",
                    appClock.getClass().getSimpleName(), appClock.instant());
            log.info("Total Spring beans registered in ApplicationContext: {}", ctx.getBeanDefinitionCount());
            log.info("=================================");
        };
    }
}
