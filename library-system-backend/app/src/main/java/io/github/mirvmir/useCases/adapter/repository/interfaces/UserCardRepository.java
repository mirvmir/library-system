package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.domain.entities.payment.UserCard;

import java.util.List;

public interface UserCardRepository {
    UserCard save(UserCard card);
    UserCard findById(Long id);
    List<UserCard> findByUserId(Long userId);
    boolean existsByUserIdAndCardToken(Long userId, String cardToken);
    UserCard findDefaultByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
