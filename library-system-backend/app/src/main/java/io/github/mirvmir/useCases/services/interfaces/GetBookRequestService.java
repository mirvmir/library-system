package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.GetBookRequestInput;
import io.github.mirvmir.useCases.services.outputs.GetBookRequestsOutput;

public interface GetBookRequestService {
    GetBookRequestsOutput execute(GetBookRequestInput input);
}
