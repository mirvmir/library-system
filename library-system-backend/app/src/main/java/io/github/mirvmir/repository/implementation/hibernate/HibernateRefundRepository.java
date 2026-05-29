package io.github.mirvmir.repository.implementation.hibernate;

import io.github.mirvmir.domain.entities.payment.Refund;
import io.github.mirvmir.domain.entities.payment.RefundStatus;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.RefundEntity;
import io.github.mirvmir.useCases.adapter.repository.interfaces.RefundRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class HibernateRefundRepository implements RefundRepository {

    private final SessionFactory sessionFactory;

    public HibernateRefundRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Refund saveOrUpdate(Refund refund) {
        Session session = sessionFactory.getCurrentSession();

        RefundEntity entity = toEntity(refund);

        if (entity.getId() == null) {
            session.persist(entity);
        } else {
            entity = session.merge(entity);
        }

        return toDomain(entity);
    }

    @Override
    public Refund findById(Long id) {
        Session session = sessionFactory.getCurrentSession();

        RefundEntity entity = session.find(RefundEntity.class, id);

        if (entity == null) {
            return null;
        }

        return toDomain(entity);
    }

    @Override
    public Refund findByExternalRefundId(String externalRefundId) {
        Session session = sessionFactory.getCurrentSession();

        RefundEntity entity = session.createQuery("""
                select r
                from RefundEntity r
                where r.externalRefundId = :externalRefundId
                """, RefundEntity.class)
                .setParameter("externalRefundId", externalRefundId)
                .uniqueResult();

        if (entity == null) {
            return null;
        }

        return toDomain(entity);
    }

    @Override
    public boolean existsByPaymentIdAndStatusIn(Long externalRefundId,
                                                Collection<RefundStatus> statuses) {
        Session session = sessionFactory.getCurrentSession();

        return Boolean.TRUE.equals(
                session.createQuery("""
                        select count(r.id) > 0
                        from RefundEntity r
                        where r.externalRefundId = :externalRefundId
                          and r.status in (:statuses)
                        """, Boolean.class)
                        .setParameter("externalRefundId", externalRefundId)
                        .setParameterList("statuses", statuses)
                        .uniqueResult()
        );
    }

    private Refund toDomain(RefundEntity entity) {
        if (entity == null) {
            return null;
        }

        return Refund.load(
                entity.getId(),
                entity.getPaymentId(),
                entity.getExternalRefundId(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getReason(),
                entity.getCreatedAt(),
                entity.getRefundedAt()
        );
    }

    private RefundEntity toEntity(Refund refund) {
        if (refund == null) {
            return null;
        }

        RefundEntity entity = new RefundEntity();

        entity.setId(refund.getId());
        entity.setPaymentId(refund.getPaymentId());
        entity.setExternalRefundId(refund.getExternalRefundId());
        entity.setPrice(refund.getPrice());
        entity.setStatus(refund.getStatus());
        entity.setReason(refund.getReason());
        entity.setCreatedAt(refund.getCreatedAt());
        entity.setRefundedAt(refund.getRefundedAt());

        return entity;
    }
}