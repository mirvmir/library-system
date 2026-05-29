package io.github.mirvmir.useCases.services;

import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultGetCompletedOrdersCountByPeriodService;
import io.github.mirvmir.useCases.services.inputs.GetCompletedOrdersCountByPeriodInput;
import io.github.mirvmir.useCases.services.outputs.GetCompletedOrdersCountByPeriodOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DefaultGetCompletedOrdersCountByPeriodServiceTest {
    private OrderRepository orderRepo;

    private DefaultGetCompletedOrdersCountByPeriodService service;

    @BeforeEach
    void setUp() {
        orderRepo = mock(OrderRepository.class);

        service = new DefaultGetCompletedOrdersCountByPeriodService(orderRepo);
    }

    @Test
    void execute_shouldReturnCompletedOrdersCount() {
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 12, 31, 0, 0);

        GetCompletedOrdersCountByPeriodInput input =
                new GetCompletedOrdersCountByPeriodInput(from, to);

        when(orderRepo.countCompletedByPeriod(from, to)).thenReturn(5L);

        GetCompletedOrdersCountByPeriodOutput result = service.execute(input);

        assertEquals(5L, result.count());
        verify(orderRepo).countCompletedByPeriod(from, to);
    }
}
