package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.domain.entities.payment.Payment;

import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);
    Payment findById(Long id);
    Payment findByIdForUpdate(Long id);
    Payment findByExternalPaymentId(String externalPaymentId);
    List<Payment> findByUserId(Long userId);
    Payment findByOrderId(Long orderId);
}
