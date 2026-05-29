package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.user.Role;
import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.exception.UnauthorizedException;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.RefreshToken;
import io.github.mirvmir.security.RefreshTokenHasher;
import io.github.mirvmir.useCases.adapter.repository.interfaces.RefreshTokenRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultRefreshService;
import io.github.mirvmir.useCases.services.outputs.RefreshOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DefaultRefreshServiceTest {
    private RefreshTokenHasher tokenHasher;
    private RefreshTokenRepository tokenRepo;
    private UserRepository userRepo;

    private DefaultRefreshService service;

    @BeforeEach
    void setUp() {
        tokenHasher = mock(RefreshTokenHasher.class);
        tokenRepo = mock(RefreshTokenRepository.class);
        userRepo = mock(UserRepository.class);

        service = new DefaultRefreshService(tokenHasher, tokenRepo, userRepo);
    }

    @Test
    void execute_shouldRefreshToken() {
        String rawToken = "12345";

        User tokenUser = mock(User.class);
        when(tokenUser.getId()).thenReturn(1L);

        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshToken.getUser()).thenReturn(tokenUser);
        when(refreshToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getRole()).thenReturn(Role.USER);
        when(tokenRepo.findByTokenHash(rawToken)).thenReturn(refreshToken);
        when(userRepo.findById(1L)).thenReturn(user);

        RefreshOutput result = service.execute(rawToken);

        assertEquals(1L, result.userId());
        assertEquals(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                result.authorities()
        );

        verify(tokenRepo).findByTokenHash(rawToken);
        verify(userRepo).findById(1L);
        verify(tokenRepo).delete(refreshToken);
        verifyNoMoreInteractions(tokenRepo, userRepo);
    }

    @Test
    void execute_shouldThrowException_whenTokenIsNull() {
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> service.execute(null)
        );

        assertEquals("Refresh token missing", exception.getMessage());

        verifyNoInteractions(tokenRepo, userRepo);
    }

    @Test
    void execute_shouldThrowException_whenTokenNotFound() {
        String rawToken = "12345";
        when(tokenRepo.findByTokenHash(rawToken)).thenReturn(null);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> service.execute(rawToken)
        );

        assertEquals("Invalid refresh token", exception.getMessage());

        verify(tokenRepo).findByTokenHash(rawToken);
        verify(userRepo, never()).findById(anyLong());
        verify(tokenRepo, never()).delete(any());
    }

    @Test
    void execute_shouldThrowException_whenTokenExpired() {
        String rawToken = "12345";

        User tokenUser = mock(User.class);
        when(tokenUser.getId()).thenReturn(1L);

        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshToken.getUser()).thenReturn(tokenUser);
        when(refreshToken.getExpiresAt()).thenReturn(Instant.now().minusSeconds(3600));

        when(tokenRepo.findByTokenHash(rawToken)).thenReturn(refreshToken);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> service.execute(rawToken)
        );

        assertEquals("Refresh token expired", exception.getMessage());

        verify(tokenRepo).findByTokenHash(rawToken);
        verify(userRepo, never()).findById(anyLong());
        verify(tokenRepo, never()).delete(any());
    }

    @Test
    void execute_shouldThrowException_whenUserNotFound() {
        String rawToken = "12345";

        User tokenUser = mock(User.class);
        when(tokenUser.getId()).thenReturn(1L);

        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshToken.getUser()).thenReturn(tokenUser);
        when(refreshToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));

        when(tokenRepo.findByTokenHash(rawToken)).thenReturn(refreshToken);
        when(userRepo.findById(1L)).thenReturn(null);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> service.execute(rawToken)
        );

        assertEquals("User not found", exception.getMessage());

        verify(tokenRepo).findByTokenHash(rawToken);
        verify(userRepo).findById(1L);
        verify(tokenRepo, never()).delete(any());
    }

    @Test
    void execute_shouldReturnAdminAuthority_whenUserRoleIsAdmin() {
        String rawToken = "12345";

        User tokenUser = mock(User.class);
        when(tokenUser.getId()).thenReturn(10L);

        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshToken.getUser()).thenReturn(tokenUser);
        when(refreshToken.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));

        User user = mock(User.class);
        when(user.getId()).thenReturn(10L);
        when(user.getRole()).thenReturn(Role.ADMIN);

        when(tokenRepo.findByTokenHash(rawToken)).thenReturn(refreshToken);
        when(userRepo.findById(10L)).thenReturn(user);

        RefreshOutput result = service.execute(rawToken);

        assertEquals(10L, result.userId());
        assertEquals(
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
                result.authorities()
        );

        verify(tokenRepo).delete(refreshToken);
    }
}