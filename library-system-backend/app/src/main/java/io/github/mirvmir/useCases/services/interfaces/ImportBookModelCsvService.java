package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.ImportBookModelCsvInput;
import io.github.mirvmir.useCases.services.outputs.ImportBookModelOutput;

public interface ImportBookModelCsvService {
    ImportBookModelOutput execute(ImportBookModelCsvInput params);
}
