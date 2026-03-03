package com.myapp.app.controllers.requests;

import java.time.LocalDateTime;

public record PeriodRq(LocalDateTime from,
                       LocalDateTime to) {
}
