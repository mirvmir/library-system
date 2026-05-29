package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.CreateBookRequestInput;
import io.github.mirvmir.useCases.services.outputs.CreateBookRequestOutput;

public interface CreateBookRequestService {
    CreateBookRequestOutput execute(CreateBookRequestInput params);
}
