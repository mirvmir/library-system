package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.CreateBookModelInput;
import com.myapp.app.useCases.services.outputs.CreateBookModelOutput;

public interface CreateBookModelService {
    CreateBookModelOutput execute(CreateBookModelInput params);
}
