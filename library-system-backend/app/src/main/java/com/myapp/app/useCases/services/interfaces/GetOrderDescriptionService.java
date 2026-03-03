package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.GetOrderDescriptionInput;
import com.myapp.app.useCases.services.outputs.OrderDescriptionOutput;

public interface GetOrderDescriptionService {
    OrderDescriptionOutput execute(GetOrderDescriptionInput params);
}
