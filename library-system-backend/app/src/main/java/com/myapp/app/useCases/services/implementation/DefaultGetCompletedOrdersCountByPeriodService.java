package com.myapp.app.useCases.services.implementation;

import com.myapp.app.useCases.adapter.repository.interfaces.OrderRepository;
import com.myapp.app.useCases.services.inputs.GetCompletedOrdersCountByPeriodInput;
import com.myapp.app.useCases.services.interfaces.GetCompletedOrdersCountByPeriodService;
import com.myapp.app.useCases.services.outputs.GetCompletedOrdersCountByPeriodOutput;
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