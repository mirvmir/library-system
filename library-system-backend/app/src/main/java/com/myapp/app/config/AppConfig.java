package com.myapp.app.config;

import com.myapp.app.TestDataSeeder;
import com.myapp.app.useCases.services.interfaces.AddBookToStockService;
import com.myapp.app.useCases.services.interfaces.CreateBookModelService;
import com.myapp.app.useCases.services.interfaces.CreateBookRequestService;
import com.myapp.app.useCases.services.interfaces.CreateCustomerService;
import com.myapp.app.useCases.services.interfaces.CreateOrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:config.properties")
@Import({
        InfraConfig.class,
        ConsoleUiConfig.class,
        RepositoryConfig.class,
        UseCasesConfig.class
})
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public TestDataSeeder testDataSeeder(
            CreateCustomerService customerService,
            CreateBookModelService bookModelService,
            AddBookToStockService bookUnitService,
            CreateOrderService orderService,
            CreateBookRequestService requestService
    ) {
        return new TestDataSeeder(
                customerService,
                bookModelService,
                bookUnitService,
                orderService,
                requestService
        );
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer configurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}