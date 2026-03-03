package com.myapp.app.useCases.services.mapping;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.useCases.services.outputs.GetBookRequestOutput;

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