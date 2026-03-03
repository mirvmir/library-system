package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.CreateCustomerInput;
import com.myapp.app.useCases.services.outputs.CreateCustomerOutput;

public interface CreateCustomerService {
    CreateCustomerOutput execute(CreateCustomerInput params);
}
