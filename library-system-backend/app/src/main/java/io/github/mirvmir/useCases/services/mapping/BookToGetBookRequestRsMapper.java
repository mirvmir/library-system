package io.github.mirvmir.useCases.services.mapping;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.useCases.services.outputs.GetBookRequestOutput;

public class BookToGetBookRequestRsMapper {
    public static GetBookRequestOutput map(BookModel book) {
        return new GetBookRequestOutput(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice().toString(),
                book.isAvailable(),
                book.getRequestCount()
        );
    }
}