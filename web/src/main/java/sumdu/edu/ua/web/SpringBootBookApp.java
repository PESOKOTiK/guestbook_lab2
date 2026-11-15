package sumdu.edu.ua.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "sumdu.edu.ua")
@EntityScan(basePackages = "sumdu.edu.ua.persistence.entity")
@EnableJpaRepositories(basePackages = "sumdu.edu.ua.persistence.repository")
public class SpringBootBookApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootBookApp.class, args);
    }
}
