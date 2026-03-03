package com.myapp.app.useCases.services.mapping;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.useCases.services.outputs.GetBookOutput;

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