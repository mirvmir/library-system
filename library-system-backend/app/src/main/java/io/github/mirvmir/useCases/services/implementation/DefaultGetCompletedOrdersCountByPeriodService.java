package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.services.inputs.GetCompletedOrdersCountByPeriodInput;
import io.github.mirvmir.useCases.services.interfaces.GetCompletedOrdersCountByPeriodService;
import io.github.mirvmir.useCases.services.outputs.GetCompletedOrdersCountByPeriodOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultGetCompletedOrdersCountByPeriodService
        implements GetCompletedOrdersCountByPeriodService {

    private final OrderRepository orderRepo;

    public DefaultGetCompletedOrdersCountByPeriodService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    @Transactional
    public GetCompletedOrdersCountByPeriodOutput execute(GetCompletedOrdersCountByPeriodInput input) {
        long completedOrdersCount = orderRepo.countCompletedByPeriod(
                input.from(),
                input.to());

        return new GetCompletedOrdersCountByPeriodOutput(completedOrdersCount);
    }
}