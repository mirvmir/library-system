package io.github.mirvmir.repository.implementation.hibernate;

import io.github.mirvmir.domain.entities.payment.Payout;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.PayoutEntity;
import io.github.mirvmir.useCases.adapter.repository.interfaces.PayoutRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class HibernatePayoutRepository implements PayoutRepository {

    private final SessionFactory sessionFactory;

    public HibernatePayoutRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Payout saveOrUpdate(Payout payout) {
        Session session = sessionFactory.getCurrentSession();

        PayoutEntity entity = toEntity(payout);

        if (entity.getId() == null) {
            session.persist(entity);
        } else {
            entity = session.merge(entity);
        }

        return toDomain(entity);
    }

    @Override
    public Payout findById(Long id) {
        Session session = sessionFactory.getCurrentSession();

        PayoutEntity entity = session.get(PayoutEntity.class, id);

        return toDomain(entity);
    }

    @Override
    public Payout findByExternalPayoutId(String externalPayoutId) {
        Session session = sessionFactory.getCurrentSession();

        PayoutEntity entity = session.createQuery("""
                from PayoutEntity p
                where p.externalPayoutId = :externalPayoutId
                """, PayoutEntity.class)
                .setParameter("externalPayoutId", externalPayoutId)
                .uniqueResult();

        return toDomain(entity);
    }

    @Override
    public boolean existsByWalletWithdrawalId(Long walletWithdrawalId) {
        Session session = sessionFactory.getCurrentSession();

        return Boolean.TRUE.equals(
                session.createQuery("""
                        select count(p.id) > 0
                        from PayoutEntity p
                        where p.walletWithdrawalId = :walletWithdrawalId
                        """, Boolean.class)
                        .setParameter("walletWithdrawalId", walletWithdrawalId)
                        .uniqueResult()
        );
    }

    private Payout toDomain(PayoutEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Payout(
                entity.getId(),
                entity.getUserId(),
                entity.getCardId(),
                entity.getExternalPayoutId(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getDescription(),
                entity.getWalletWithdrawalId(),
                entity.getCreatedAt(),
                entity.getPaidAt()
        );
    }

    private PayoutEntity toEntity(Payout payout) {
        if (payout == null) {
            return null;
        }

        PayoutEntity entity = new PayoutEntity();

        entity.setId(payout.getId());
        entity.setUserId(payout.getUserId());
        entity.setCardId(payout.getCardId());
        entity.setExternalPayoutId(payout.getExternalPayoutId());
        entity.setPrice(payout.getPrice());
        entity.setStatus(payout.getStatus());
        entity.setDescription(payout.getDescription());
        entity.setWalletWithdrawalId(payout.getWalletWithdrawalId());
        entity.setCreatedAt(payout.getCreatedAt());
        entity.setPaidAt(payout.getPaidAt());

        return entity;
    }
}