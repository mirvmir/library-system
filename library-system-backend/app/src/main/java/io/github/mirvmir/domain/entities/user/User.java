package io.github.mirvmir.domain.entities.user;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected User() {
    };

    public User(Role role, String passwordHash, String email) {
        this.role = role;
        this.passwordHash = passwordHash;
        this.email = email;
    };

    public int hashCode() {
        return Long.hashCode(id);
    }

    public static User createNewCustomer(String passwordHash, String email) {
        return new User(Role.USER, passwordHash, email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || getClass() != o.getClass()) {
            return false;
        }

        User other = (User) o;
        return id != null && id.equals(other.id);
    }

    public Long getId() {
        return this.id;
    }

    public void setAdmin() {
        this.role = Role.ADMIN;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }
}
