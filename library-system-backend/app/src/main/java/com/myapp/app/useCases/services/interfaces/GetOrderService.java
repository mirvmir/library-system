package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.GetOrderInput;
import com.myapp.app.useCases.services.outputs.GetOrdersOutput;

public interface GetOrderService {
    GetOrdersOutput execute(GetOrderInput request);
}
