package com.myapp.app.useCases.services.implementation;

import com.myapp.app.useCases.adapter.repository.interfaces.OrderRepository;
import com.myapp.app.useCases.services.interfaces.CalculateTotalEarningsByPeriodService;
import com.myapp.app.useCases.services.inputs.CalculateTotalEarningsByPeriodInput;
import com.myapp.app.useCases.services.outputs.CalculateTotalEarningsByPeriodOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class DefaultCalculateTotalEarningsByPeriodService implements CalculateTotalEarningsByPeriodService {

    private final OrderRepository orderRepo;

    public DefaultCalculateTotalEarningsByPeriodService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    @Transactional
    public CalculateTotalEarningsByPeriodOutput execute(CalculateTotalEarningsByPeriodInput params) {
        BigDecimal totalEarnings = orderRepo.calculateTotalEarningsByPeriod(
                params.from(),
                params.to()
        );

        return new CalculateTotalEarningsByPeriodOutput(totalEarnings);
    }
}