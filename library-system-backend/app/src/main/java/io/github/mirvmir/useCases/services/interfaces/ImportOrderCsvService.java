package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.ImportOrderCsvInput;
import io.github.mirvmir.useCases.services.outputs.ImportOrderOutput;

public interface ImportOrderCsvService {
    ImportOrderOutput execute(ImportOrderCsvInput params);
}
