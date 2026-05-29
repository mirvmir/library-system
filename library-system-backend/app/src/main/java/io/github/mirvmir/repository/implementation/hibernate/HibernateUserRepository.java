package io.github.mirvmir.repository.implementation.hibernate;

import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateUserRepository implements UserRepository {

    private final SessionFactory sessionFactory;

    public HibernateUserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User save(User user) {
        Session session = sessionFactory.getCurrentSession();

        session.persist(user);
        return user;
    }

    @Override
    public boolean existUser(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(User.class, userId) != null;
    }

    @Override
    public User findById(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(User.class, userId);
    }

    @Override
    public User findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "FROM User WHERE email = :email", User.class
                )
                .setParameter("email", email)
                .uniqueResult();
    }
}
