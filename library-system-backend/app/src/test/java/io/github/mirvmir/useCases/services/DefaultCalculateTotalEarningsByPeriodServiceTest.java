package io.github.mirvmir.useCases.services;

import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultCalculateTotalEarningsByPeriodService;
import io.github.mirvmir.useCases.services.inputs.CalculateTotalEarningsByPeriodInput;
import io.github.mirvmir.useCases.services.outputs.CalculateTotalEarningsByPeriodOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DefaultCalculateTotalEarningsByPeriodServiceTest {
    private OrderRepository orderRepo;

    private DefaultCalculateTotalEarningsByPeriodService service;

    @BeforeEach
    void setUp() {
        orderRepo = mock(OrderRepository.class);

        service = new DefaultCalculateTotalEarningsByPeriodService(orderRepo);
    }

    @Test
    void execute_shouldReturnTotalEarnings() {
        BigDecimal totalEarnings = new BigDecimal("1500.50");

        CalculateTotalEarningsByPeriodInput input =
                new CalculateTotalEarningsByPeriodInput(
                        LocalDateTime.of(2024, 1, 1, 0,0),
                        LocalDateTime.of(2024, 12, 31, 0, 0)
                );

        when(orderRepo.calculateTotalEarningsByPeriod(
                input.from(),
                input.to()
        )).thenReturn(totalEarnings);

        CalculateTotalEarningsByPeriodOutput result = service.execute(input);

        assertEquals(totalEarnings, result.totalEarnings());

        // не избытоен ли этот тест...
        verify(orderRepo).calculateTotalEarningsByPeriod(
                input.from(),
                input.to()
        );
    }
}
