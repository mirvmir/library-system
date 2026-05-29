package io.github.mirvmir.useCases.services.inputs;

import java.time.LocalDateTime;

public record GetCompletedOrdersCountByPeriodInput(LocalDateTime from,
                                                   LocalDateTime to) {
}
