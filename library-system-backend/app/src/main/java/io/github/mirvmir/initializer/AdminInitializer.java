package io.github.mirvmir.initializer;

import io.github.mirvmir.useCases.services.implementation.AdminInitService;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class AdminInitializer {

    private final AdminInitService adminInitService;

    public AdminInitializer(AdminInitService adminInitService) {
        this.adminInitService = adminInitService;
    }

    @PostConstruct
    public void init() {
        adminInitService.initAdmin();
    }
}