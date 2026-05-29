package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.BookDescriptionInput;
import io.github.mirvmir.useCases.services.outputs.BookDescriptionOutput;

public interface GetBookDescriptionService {
    BookDescriptionOutput execute(BookDescriptionInput input);
}
