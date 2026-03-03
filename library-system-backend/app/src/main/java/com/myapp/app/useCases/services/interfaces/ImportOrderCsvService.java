package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.ImportOrderCsvInput;
import com.myapp.app.useCases.services.outputs.ImportOrderOutput;

public interface ImportOrderCsvService {
    ImportOrderOutput execute(ImportOrderCsvInput params);
}
