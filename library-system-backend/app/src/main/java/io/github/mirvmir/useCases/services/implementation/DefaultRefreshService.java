package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.user.Role;
import io.github.mirvmir.exception.UnauthorizedException;
import io.github.mirvmir.useCases.services.outputs.RefreshOutput;
import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.RefreshToken;
import io.github.mirvmir.security.RefreshTokenHasher;
import io.github.mirvmir.useCases.adapter.repository.interfaces.RefreshTokenRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import io.github.mirvmir.useCases.services.interfaces.RefreshService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class DefaultRefreshService implements RefreshService {

    private final RefreshTokenHasher tokenHasher;

    private final RefreshTokenRepository tokenRepo;
    private final UserRepository userRepo;

    public DefaultRefreshService(RefreshTokenHasher tokenHasher,
                                 RefreshTokenRepository tokenRepo,
                                 UserRepository userRepo) {
        this.tokenHasher = tokenHasher;
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    @Override
    public RefreshOutput execute(String rawToken) {
        if (null == rawToken) {
            throw new UnauthorizedException("Refresh token missing");
        }

        RefreshToken refreshToken = tokenRepo.findByTokenHash(rawToken);

        if (null == refreshToken) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }

        User user = userRepo.findById(refreshToken.getUser().getId());
        if (null == user) {
            throw new UnauthorizedException("User not found");
        }

        tokenRepo.delete(refreshToken);

        Role role = user.getRole();
        return new RefreshOutput(
                user.getId(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
        );
    }
}
