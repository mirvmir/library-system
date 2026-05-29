package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminInitService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;

    public AdminInitService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initAdmin() {
        if (!userRepository.existUser(1L)) {
            User admin = User.createNewCustomer(
                    passwordEncoder.encode(adminPassword),
                    adminEmail
            );
            admin.setAdmin();

            userRepository.save(admin);
        }
    }
}