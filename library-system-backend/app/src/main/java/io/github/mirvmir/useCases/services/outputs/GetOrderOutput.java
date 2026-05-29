package io.github.mirvmir.useCases.services.outputs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record GetOrderOutput(Long id,
                             Long customerId,
                             String status,
                             LocalDateTime completionDate,
                             BigDecimal totalPrice,
                             List<String> isbns) {
}
