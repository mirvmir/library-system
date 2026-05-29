package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.AddBookToStockInput;
import io.github.mirvmir.useCases.services.outputs.AddBookToStockOutput;

public interface AddBookToStockService {
    AddBookToStockOutput execute(AddBookToStockInput params);
}
