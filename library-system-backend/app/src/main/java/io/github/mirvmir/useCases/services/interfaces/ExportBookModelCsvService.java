package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.ExportBookModelCsvInput;
import io.github.mirvmir.useCases.services.outputs.ExportBookModelOutput;

public interface ExportBookModelCsvService {
    ExportBookModelOutput execute(ExportBookModelCsvInput params);
}
