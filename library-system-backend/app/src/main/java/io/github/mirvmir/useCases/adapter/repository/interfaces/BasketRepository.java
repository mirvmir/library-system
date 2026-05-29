package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.BasketEntity;

public interface BasketRepository {
    BasketEntity findByUserId(Long userId);
}
