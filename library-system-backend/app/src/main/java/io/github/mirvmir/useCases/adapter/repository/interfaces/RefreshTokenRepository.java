package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.RefreshToken;

public interface RefreshTokenRepository {
    RefreshToken findByTokenHash(String tokenHash);
    RefreshToken save(RefreshToken refreshToken);
    void delete(RefreshToken refreshToken);
}
