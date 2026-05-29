package io.github.mirvmir.config;

import io.github.mirvmir.repository.implementation.hibernate.*;
import io.github.mirvmir.useCases.adapter.repository.interfaces.*;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("io.github.mirvmir.repository")
public class RepositoryConfig {

    @Bean
    public HibernateBasketRepository basketRepository(SessionFactory sf) {
        return new HibernateBasketRepository(sf);
    }

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
    public OrderRepository orderRepository(SessionFactory sf) {
        return new HibernateOrderRepository(sf);
    }

    @Bean
    public UserRepository customerRepository(SessionFactory sf) {
        return new HibernateUserRepository(sf);
    }
}