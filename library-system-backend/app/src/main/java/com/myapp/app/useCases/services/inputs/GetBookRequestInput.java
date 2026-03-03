package com.myapp.app.useCases.services.inputs;

public record GetBookRequestInput(String type,
                                  String direction,
                                  String field) {
}
