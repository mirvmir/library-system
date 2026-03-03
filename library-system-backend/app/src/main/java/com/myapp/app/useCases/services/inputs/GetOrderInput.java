package com.myapp.app.useCases.services.inputs;

import java.time.LocalDateTime;

public record GetOrderInput(String type,
                            boolean filtered,
                            String direction,
                            String field,
                            LocalDateTime from,
                            LocalDateTime to) {
}
