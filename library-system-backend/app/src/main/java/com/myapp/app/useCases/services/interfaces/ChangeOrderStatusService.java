package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.ChangeOrderStatusInput;
import com.myapp.app.useCases.services.outputs.ChangeOrderStatusOutput;

public interface ChangeOrderStatusService {
    ChangeOrderStatusOutput execute(ChangeOrderStatusInput params);
}
