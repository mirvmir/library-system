package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.domain.entities.payment.Refund;
import io.github.mirvmir.domain.entities.payment.RefundStatus;

import java.util.Collection;

public interface RefundRepository {
    Refund saveOrUpdate(Refund refund);
    Refund findById(Long id);
    Refund findByExternalRefundId(String externalRefundId);
    boolean existsByPaymentIdAndStatusIn(Long paymentId,
                                         Collection<RefundStatus> statuses);
}
