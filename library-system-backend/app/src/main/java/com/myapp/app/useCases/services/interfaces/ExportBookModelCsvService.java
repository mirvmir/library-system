package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.ExportBookModelCsvInput;
import com.myapp.app.useCases.services.outputs.ExportBookModelOutput;

public interface ExportBookModelCsvService {
    ExportBookModelOutput execute(ExportBookModelCsvInput params);
}
