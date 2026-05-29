package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.domain.entities.payment.Payout;

public interface PayoutRepository {
    Payout saveOrUpdate(Payout payout);
    Payout findById(Long id);
    Payout findByExternalPayoutId(String externalPayoutId);
    boolean existsByWalletWithdrawalId(Long walletWithdrawalId);
}
