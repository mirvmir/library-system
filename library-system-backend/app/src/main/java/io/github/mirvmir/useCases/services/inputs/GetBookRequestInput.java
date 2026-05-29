package io.github.mirvmir.useCases.services.inputs;

public record GetBookRequestInput(String type,
                                  String direction,
                                  String field) {
}
