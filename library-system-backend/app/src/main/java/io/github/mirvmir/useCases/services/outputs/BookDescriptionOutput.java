package io.github.mirvmir.useCases.services.outputs;

import java.math.BigDecimal;

public record BookDescriptionOutput(String isbn,
                                    String title,
                                    String author,
                                    BigDecimal price,
                                    boolean available) {
}
