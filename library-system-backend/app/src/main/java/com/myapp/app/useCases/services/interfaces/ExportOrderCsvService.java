package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.ExportOrderCsvInput;
import com.myapp.app.useCases.services.outputs.ExportOrderOutput;

public interface ExportOrderCsvService {
    ExportOrderOutput execute(ExportOrderCsvInput params);
}
