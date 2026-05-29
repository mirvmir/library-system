package io.github.mirvmir.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Clock;

@Configuration
@PropertySource("classpath:config.properties")
@ComponentScan("io.github.mirvmir.security")
@ComponentScan("io.github.mirvmir.initializer")
@ComponentScan("io.github.mirvmir.event")
@Import({
        InfraConfig.class,
        RepositoryConfig.class,
        UseCasesConfig.class,
        WebClientConfig.class
})
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer configurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}