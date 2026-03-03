package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.BookDescriptionInput;
import com.myapp.app.useCases.services.outputs.BookDescriptionOutput;

public interface GetBookDescriptionService {
    BookDescriptionOutput execute(BookDescriptionInput params);
}
