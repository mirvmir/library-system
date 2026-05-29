package io.github.mirvmir.repository.implementation.hibernate;

import io.github.mirvmir.domain.entities.payment.Payment;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.PaymentEntity;
import io.github.mirvmir.useCases.adapter.repository.interfaces.PaymentRepository;
import jakarta.persistence.LockModeType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HibernatePaymentRepository implements PaymentRepository {

    private final SessionFactory sessionFactory;

    public HibernatePaymentRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Payment save(Payment payment) {
        Session session = sessionFactory.getCurrentSession();

        PaymentEntity entity = toEntity(payment);

        if (entity.getId() == null) {
            session.persist(entity);
        } else {
            entity = session.merge(entity);
        }

        return toDomain(entity);
    }

    @Override
    public Payment findById(Long paymentId) {
        Session session = sessionFactory.getCurrentSession();

        PaymentEntity entity = session.find(PaymentEntity.class, paymentId);

        return toDomain(entity);
    }

    @Override
    public Payment findByIdForUpdate(Long paymentId) {
        PaymentEntity entity = sessionFactory.getCurrentSession()
                .find(
                        PaymentEntity.class,
                        paymentId,
                        LockModeType.PESSIMISTIC_WRITE
                );

        return toDomain(entity);
    }

    @Override
    public Payment findByExternalPaymentId(String externalPaymentId) {
        Session session = sessionFactory.getCurrentSession();

        PaymentEntity entity = session.createQuery("""
                select p
                from PaymentEntity p
                where p.externalPaymentId = :externalPaymentId
                """, PaymentEntity.class)
                .setParameter("externalPaymentId", externalPaymentId)
                .uniqueResult();

        return toDomain(entity);
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("""
                select p
                from PaymentEntity p
                where p.userId = :userId
                order by p.createdAt desc
                """, PaymentEntity.class)
                .setParameter("userId", userId)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Payment findByOrderId(Long orderId) {
        Session session = sessionFactory.getCurrentSession();

        PaymentEntity entity = session.createQuery("""
                select p
                from PaymentEntity p
                where p.orderId = :orderId
                """, PaymentEntity.class)
                .setParameter("orderId", orderId)
                .uniqueResult();

        return toDomain(entity);
    }

    private Payment toDomain(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Payment(
                entity.getId(),
                entity.getUserId(),
                entity.getExternalPaymentId(),
                entity.getPrice(),
                entity.getStatus(),
                entity.getDescription(),
                entity.getOrderId(),
                entity.getCreatedAt(),
                entity.getPaidAt()
        );
    }

    private PaymentEntity toEntity(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentEntity entity = new PaymentEntity();

        entity.setId(payment.getId());
        entity.setUserId(payment.getUserId());
        entity.setExternalPaymentId(payment.getExternalPaymentId());
        entity.setPrice(payment.getPrice());
        entity.setStatus(payment.getStatus());
        entity.setDescription(payment.getDescription());
        entity.setOrderId(payment.getOrderId());
        entity.setCreatedAt(payment.getCreatedAt());
        entity.setPaidAt(payment.getPaidAt());

        return entity;
    }
}