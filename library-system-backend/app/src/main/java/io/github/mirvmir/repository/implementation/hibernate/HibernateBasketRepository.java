package io.github.mirvmir.repository.implementation.hibernate;

import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.BasketEntity;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BasketRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateBasketRepository implements BasketRepository {

    private final SessionFactory sessionFactory;

    public HibernateBasketRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public BasketEntity findByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("""
                SELECT DISTINCT b
                FROM BasketEntity b
                LEFT JOIN FETCH b.models
                WHERE b.user.id = :userId
                """, BasketEntity.class)
                .setParameter("userId", userId)
                .uniqueResult();
    }
}
