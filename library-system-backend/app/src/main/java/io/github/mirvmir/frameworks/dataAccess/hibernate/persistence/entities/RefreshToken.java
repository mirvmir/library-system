package io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities;

import io.github.mirvmir.domain.entities.user.User;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    protected RefreshToken() {
    }

    public RefreshToken(User user, String tokenHash, Duration expiration) {
        this.user = user;
        this.tokenHash = tokenHash;

        Instant now = Instant.now();
        this.createdAt = Instant.from(now);
        this.expiresAt = Instant.from(now.plus(expiration));
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public User getUser() {
        return user;
    }
}
