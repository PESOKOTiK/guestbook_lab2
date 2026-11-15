package sumdu.edu.ua.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CatalogConfig {

    @Bean
    public Clock appClock() {
        return Clock.systemUTC();
    }
}
