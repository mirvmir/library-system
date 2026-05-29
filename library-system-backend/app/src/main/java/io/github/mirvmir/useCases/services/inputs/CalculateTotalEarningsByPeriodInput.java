package io.github.mirvmir.useCases.services.inputs;

import java.time.LocalDateTime;

public record CalculateTotalEarningsByPeriodInput(LocalDateTime from,
                                                  LocalDateTime to) {
}
