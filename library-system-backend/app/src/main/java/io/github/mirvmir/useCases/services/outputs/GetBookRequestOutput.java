package io.github.mirvmir.useCases.services.outputs;

public record GetBookRequestOutput(String isbn,
                                   String title,
                                   String author,
                                   String price,
                                   boolean available,
                                   int requestCount) {
}
