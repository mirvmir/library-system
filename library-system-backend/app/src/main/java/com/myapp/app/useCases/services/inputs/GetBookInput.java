package com.myapp.app.useCases.services.inputs;

public record GetBookInput(String type,
                           String direction,
                           String field) {
}
