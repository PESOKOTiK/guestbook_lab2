package sumdu.edu.ua.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.Map;

@Configuration
public class CatalogConfig {
    private static final Logger log = LoggerFactory.getLogger(CatalogConfig.class);

    @Bean
    public Clock appClock() {
        log.info("Creating custom @Bean 'appClock' (UTC system clock)");
        return Clock.systemUTC();
    }

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
}
