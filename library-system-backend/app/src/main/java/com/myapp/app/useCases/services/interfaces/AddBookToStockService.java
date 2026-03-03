package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.AddBookToStockInput;
import com.myapp.app.useCases.services.outputs.AddBookToStockOutput;

public interface AddBookToStockService {
    AddBookToStockOutput execute(AddBookToStockInput params);
}
