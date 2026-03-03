package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.ImportBookModelCsvInput;
import com.myapp.app.useCases.services.outputs.ImportBookModelOutput;

public interface ImportBookModelCsvService {
    ImportBookModelOutput execute(ImportBookModelCsvInput params);
}
