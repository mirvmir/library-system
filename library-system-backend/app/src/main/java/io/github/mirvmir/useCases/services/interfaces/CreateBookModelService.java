package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.CreateBookModelInput;
import io.github.mirvmir.useCases.services.outputs.CreateBookModelOutput;

public interface CreateBookModelService {
    CreateBookModelOutput execute(CreateBookModelInput params);
}
