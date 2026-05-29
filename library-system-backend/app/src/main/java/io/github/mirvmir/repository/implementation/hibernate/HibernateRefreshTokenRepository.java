package io.github.mirvmir.repository.implementation.hibernate;

import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.RefreshToken;
import io.github.mirvmir.useCases.adapter.repository.interfaces.RefreshTokenRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateRefreshTokenRepository implements RefreshTokenRepository {

    private final SessionFactory sessionFactory;

    public HibernateRefreshTokenRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public RefreshToken findByTokenHash(String tokenHash) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "FROM RefreshToken WHERE tokenHash = :tokenHash", RefreshToken.class
                )
                .setParameter("tokenHash", tokenHash)
                .uniqueResult();
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        Session session = sessionFactory.getCurrentSession();

        session.persist(refreshToken);
        return refreshToken;
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        Session session = sessionFactory.getCurrentSession();
        session.remove(refreshToken);
    }
}
