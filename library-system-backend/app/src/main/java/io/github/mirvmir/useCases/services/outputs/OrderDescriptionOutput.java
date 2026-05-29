package io.github.mirvmir.useCases.services.outputs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDescriptionOutput(Long id,
                                     Long customerId,
                                     String status,
                                     LocalDateTime completionDate,
                                     LocalDateTime createdDate,
                                     BigDecimal totalPrice,
                                     List<String> isbns) {
}
