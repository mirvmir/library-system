package com.myapp.app.useCases.services.inputs;

import java.time.LocalDateTime;

public record CalculateTotalEarningsByPeriodInput(LocalDateTime from,
                                                  LocalDateTime to) {
}
