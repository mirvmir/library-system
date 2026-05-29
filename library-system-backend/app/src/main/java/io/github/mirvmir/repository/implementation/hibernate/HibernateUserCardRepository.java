package io.github.mirvmir.repository.implementation.hibernate;

import io.github.mirvmir.domain.entities.payment.UserCard;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.UserCardEntity;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserCardRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HibernateUserCardRepository implements UserCardRepository {

    private final SessionFactory sessionFactory;

    public HibernateUserCardRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UserCard save(UserCard card) {
        Session session = sessionFactory.getCurrentSession();

        UserCardEntity entity = toEntity(card);

        if (entity.getId() == null) {
            session.persist(entity);
        } else {
            entity = session.merge(entity);
        }

        return toDomain(entity);
    }

    @Override
    public UserCard findById(Long id) {
        Session session = sessionFactory.getCurrentSession();

        UserCardEntity entity = session.find(UserCardEntity.class, id);

        return toDomain(entity);
    }

    @Override
    public List<UserCard> findByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("""
                select c
                from UserCardEntity c
                where c.userId = :userId
                  and c.active = true
                order by c.createdAt desc
                """, UserCardEntity.class)
                .setParameter("userId", userId)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndCardToken(Long userId, String cardToken) {
        Session session = sessionFactory.getCurrentSession();

        return Boolean.TRUE.equals(
                session.createQuery("""
                        select count(c.id) > 0
                        from UserCardEntity c
                        where c.userId = :userId
                          and c.cardToken = :cardToken
                        """, Boolean.class)
                        .setParameter("userId", userId)
                        .setParameter("cardToken", cardToken)
                        .uniqueResult()
        );
    }

    @Override
    public UserCard findDefaultByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();

        UserCardEntity entity = session.createQuery("""
                select c
                from UserCardEntity c
                where c.userId = :userId
                  and c.active = true
                  and c.defaultCard = true
                """, UserCardEntity.class)
                .setParameter("userId", userId)
                .uniqueResult();

        return toDomain(entity);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();

        return Boolean.TRUE.equals(
                session.createQuery("""
                        select count(c.id) > 0
                        from UserCardEntity c
                        where c.userId = :userId
                        """, Boolean.class)
                        .setParameter("userId", userId)
                        .uniqueResult()
        );
    }

    private UserCardEntity toEntity(UserCard userCard) {
        if (userCard == null) {
            return null;
        }

        UserCardEntity entity = new UserCardEntity();

        entity.setId(userCard.getId());
        entity.setUserId(userCard.getUserId());
        entity.setBankCardId(userCard.getBankCardId());
        entity.setCardToken(userCard.getCardToken());
        entity.setMaskedPan(userCard.getMaskedPan());
        entity.setLast4(userCard.getLast4());
        entity.setPaymentSystem(userCard.getPaymentSystem());
        entity.setActive(userCard.isActive());
        entity.setDefaultCard(userCard.isDefaultCard());
        entity.setCreatedAt(userCard.getCreatedAt());
        entity.setUpdatedAt(userCard.getUpdatedAt());

        return entity;
    }

    private UserCard toDomain(UserCardEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserCard(
                entity.getId(),
                entity.getUserId(),
                entity.getBankCardId(),
                entity.getCardToken(),
                entity.getMaskedPan(),
                entity.getLast4(),
                entity.getPaymentSystem(),
                entity.isActive(),
                entity.isDefaultCard(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}