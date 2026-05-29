package io.github.mirvmir.useCases.services.outputs;

public record GetBookOutput(String isbn,
                            String title,
                            String author,
                            String price,
                            boolean available) {
}
