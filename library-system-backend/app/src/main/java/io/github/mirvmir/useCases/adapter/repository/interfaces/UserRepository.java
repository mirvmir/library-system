package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.domain.entities.user.User;

public interface UserRepository {
    User save(User user);
    boolean existUser(Long userId);
    User findById(Long userId);
    User findByEmail(String email);
}
