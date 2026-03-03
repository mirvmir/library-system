package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.CompleteOrderInput;
import com.myapp.app.useCases.services.outputs.CompleteOrderOutput;

public interface CompleteOrderService {
    CompleteOrderOutput execute(CompleteOrderInput params);
}
