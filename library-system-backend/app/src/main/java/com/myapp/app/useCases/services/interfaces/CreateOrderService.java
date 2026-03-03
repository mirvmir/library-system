package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.CreateOrderInput;
import com.myapp.app.useCases.services.outputs.CreateOrderOutput;

public interface CreateOrderService {
    CreateOrderOutput execute(CreateOrderInput params);
}
