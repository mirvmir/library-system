package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.GetBookRequestInput;
import com.myapp.app.useCases.services.outputs.GetBookRequestsOutput;

public interface GetBookRequestService {
    GetBookRequestsOutput execute(GetBookRequestInput request);
}
