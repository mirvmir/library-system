package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.GetBookInput;
import io.github.mirvmir.useCases.services.outputs.GetBooksOutput;

public interface GetBookService {
    GetBooksOutput execute(GetBookInput params);
}
