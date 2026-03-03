package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.CreateBookRequestInput;
import com.myapp.app.useCases.services.outputs.CreateBookRequestOutput;

public interface CreateBookRequestService {
    CreateBookRequestOutput execute(CreateBookRequestInput params);
}
