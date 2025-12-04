package sumdu.edu.ua.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration class for custom beans.
 * Demonstrates @Configuration and @Bean annotations for defining custom beans.
 */
@Configuration
public class AppConfig {

    /**
     * Custom bean definition using @Bean annotation.
     * This ObjectMapper bean will be used for JSON serialization/deserialization.
     * Spring will manage this bean's lifecycle and make it available for dependency injection.
     *
     * @return configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

