package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.CancelOrderInput;
import com.myapp.app.useCases.services.outputs.CancelOrderOutput;

public interface CancelOrderService {
    CancelOrderOutput execute(CancelOrderInput params);
}
