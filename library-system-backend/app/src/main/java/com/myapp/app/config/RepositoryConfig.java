package com.myapp.app.config;

import com.myapp.app.repository.implementation.hibernate.*;
import com.myapp.app.useCases.adapter.repository.interfaces.*;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.myapp.app.repository")
public class RepositoryConfig {

    @Bean
    public BookModelRepository bookModelRepository(SessionFactory sf) {
        return new HibernateBookModelRepository(sf);
    }

    @Bean
    public BookRequestRepository bookRequestRepository(SessionFactory sf) {
        return new HibernateBookRequestRepository(sf);
    }

    @Bean
    public BookUnitRepository bookUnitRepository(SessionFactory sf) {
        return new HibernateBookUnitRepository(sf);
    }

    @Bean
    public CustomerRepository customerRepository(SessionFactory sf) {
        return new HibernateCustomerRepository(sf);
    }

    @Bean
    public OrderRepository orderRepository(SessionFactory sf) {
        return new HibernateOrderRepository(sf);
    }
}