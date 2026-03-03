package com.myapp.app.useCases.services.outputs;

public record GetBookRequestOutput(String isbn,
                                   String title,
                                   String author,
                                   String price,
                                   boolean available,
                                   int requestCount) {
}
