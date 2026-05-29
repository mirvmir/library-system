package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.user.Role;
import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultCreateAdminService;
import io.github.mirvmir.useCases.services.inputs.CreateAdminInput;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class DefaultCreateAdminServiceTest {
    private UserRepository userRepo;

    private DefaultCreateAdminService service;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepository.class);

        service = new DefaultCreateAdminService(userRepo);
    }

    @Test
    void execute_shouldCreateAdmin() {
        CreateAdminInput input = new CreateAdminInput(1L);
        User user = new User(Role.USER, "password_hash", "admin@example.com");
        // user.setId(1L) -- надо бы сделать, наверное
        when(userRepo.findById(input.userId()))
                .thenReturn(user);

        service.execute(input);

        verify(userRepo).save(user);

        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void execute_shouldNotCreateAdmin_shouldThrowException_whenUserNotFound() {
        CreateAdminInput input = new CreateAdminInput(1L);
        when(userRepo.findById(input.userId()))
                .thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.execute(input)
        );
        assertEquals("User with ID " + input.userId() + " not found.", exception.getMessage());

        verify(userRepo, never()).save(any(User.class));
    }
}
