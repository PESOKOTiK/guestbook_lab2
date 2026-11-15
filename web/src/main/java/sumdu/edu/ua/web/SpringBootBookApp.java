package sumdu.edu.ua.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "sumdu.edu.ua")
public class SpringBootBookApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootBookApp.class, args);
    }
}
