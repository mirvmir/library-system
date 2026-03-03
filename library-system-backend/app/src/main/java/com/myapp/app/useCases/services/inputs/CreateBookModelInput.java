package com.myapp.app.useCases.services.inputs;

import java.math.BigDecimal;

public record CreateBookModelInput(String isbn, String title, String author, BigDecimal price) {
    public CreateBookModelInput {
    }
}