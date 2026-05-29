package io.github.mirvmir.useCases.services;

import io.github.mirvmir.security.JwtService;
import io.github.mirvmir.security.RefreshTokenHasher;
import io.github.mirvmir.useCases.adapter.repository.interfaces.RefreshTokenRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

public class DefaultAuthServiceTest {
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private RefreshTokenHasher refreshTokenHasher;
    private PasswordEncoder passwordEncoder;

    private UserRepository userRepo;
    private RefreshTokenRepository tokenRepo;

    private DefaultAuthService service;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtService = mock(JwtService.class);
        refreshTokenHasher = mock(RefreshTokenHasher.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userRepo = mock(UserRepository.class);
        tokenRepo = mock(RefreshTokenRepository.class);

        service = new DefaultAuthService(
                authenticationManager,
                jwtService,
                refreshTokenHasher,
                passwordEncoder,
                userRepo,
                tokenRepo
        );
    }
}
