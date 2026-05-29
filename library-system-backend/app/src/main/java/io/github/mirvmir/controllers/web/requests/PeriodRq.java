package io.github.mirvmir.controllers.web.requests;

import java.time.LocalDateTime;

public record PeriodRq(LocalDateTime from,
                       LocalDateTime to) {
}
