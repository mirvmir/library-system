package io.github.mirvmir.useCases.services.mapping;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.useCases.services.outputs.GetBookOutput;

public class BookToGetBookRsMapper {
    public static GetBookOutput map(BookModel book) {
        return new GetBookOutput(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice().toString(),
                book.isAvailable()
        );
    }
}