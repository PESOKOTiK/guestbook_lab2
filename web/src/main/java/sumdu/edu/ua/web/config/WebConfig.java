package sumdu.edu.ua.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "sumdu.edu.ua")
public class WebConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(objectMapper());
        jsonConverter.setDefaultCharset(StandardCharsets.UTF_8);
        jsonConverter.setSupportedMediaTypes(List.of(
                new MediaType("application", "json", StandardCharsets.UTF_8),
                MediaType.APPLICATION_JSON
        ));
        converters.add(jsonConverter);
        log.info("Spring MVC WebConfig (@EnableWebMvc): Configured MappingJackson2HttpMessageConverter with UTF-8");
    }

    @Bean
    public CommandLineRunner verifyDispatcherServletRunner(ApplicationContext ctx) {
        return args -> {
            boolean hasDispatcher = ctx.containsBean("dispatcherServlet");
            DispatcherServlet ds = ctx.getBean(DispatcherServlet.class);
            log.info("=== [SPRING MVC DISPATCHER SERVLET VERIFICATION] ===");
            log.info("DispatcherServlet registered in ApplicationContext: {} (class={})",
                    hasDispatcher, ds.getClass().getName());
            log.info("WebConfig active with @EnableWebMvc: true");
            log.info("====================================================");
        };
    }
}
