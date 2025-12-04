package sumdu.edu.ua;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class.
 * This class serves as the entry point for the application and enables
 * Spring Boot auto-configuration through the @SpringBootApplication annotation.
 * 
 * The scanBasePackages parameter explicitly specifies the base package
 * for component scanning.
 */
@SpringBootApplication(scanBasePackages = "sumdu.edu.ua")
public class AppInit {

    public static void main(String[] args) {
        SpringApplication.run(AppInit.class, args);
        System.out.println("\n=== Spring Boot Books Application Started ===");
        System.out.println("Application URL: http://localhost:8080/books");
        System.out.println("API URL: http://localhost:8080/api/books\n");
    }
}

