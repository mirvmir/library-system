package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import io.github.mirvmir.useCases.services.implementation.AdminInitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class AdminInitServiceTest {
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private AdminInitService service;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new AdminInitService(userRepo, passwordEncoder);

        ReflectionTestUtils.setField(service, "adminEmail", "admin@example.com");
        ReflectionTestUtils.setField(service, "adminPassword", "123");
    }

    @Test
    void initAdmin_shouldCreateAdmin() {
        when(passwordEncoder.encode("123"))
                .thenReturn("hashed_password");
        when(userRepo.existUser(1L))
                .thenReturn(false);

        service.initAdmin();

        verify(passwordEncoder).encode("123");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void initAdmin_shouldNotCreateAdmin_UserAlreadyExists() {
        when(userRepo.existUser(1L)).
                thenReturn(true);

        assertDoesNotThrow(() -> service.initAdmin());

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepo, never()).save(any(User.class));
    }
}
