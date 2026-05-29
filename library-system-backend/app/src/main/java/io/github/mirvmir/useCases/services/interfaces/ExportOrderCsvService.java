package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.ExportOrderCsvInput;
import io.github.mirvmir.useCases.services.outputs.ExportOrderOutput;

public interface ExportOrderCsvService {
    ExportOrderOutput execute(ExportOrderCsvInput params);
}
