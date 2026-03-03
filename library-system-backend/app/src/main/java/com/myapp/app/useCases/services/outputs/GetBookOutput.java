package com.myapp.app.useCases.services.outputs;

public record GetBookOutput(String isbn,
                            String title,
                            String author,
                            String price,
                            boolean available) {
}
